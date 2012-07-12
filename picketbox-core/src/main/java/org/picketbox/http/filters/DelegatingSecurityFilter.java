/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketbox.http.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.picketbox.PicketBoxMessages;
import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authentication.http.HTTPAuthenticationSchemeLoader;
import org.picketbox.authentication.http.impl.HTTPBasicAuthenticationSchemeLoader;
import org.picketbox.authentication.http.impl.HTTPClientCertAuthenticationSchemeLoader;
import org.picketbox.authentication.http.impl.HTTPDigestAuthenticationSchemeLoader;
import org.picketbox.authentication.http.impl.HTTPFormAuthenticationSchemeLoader;
import org.picketbox.authentication.impl.PropertiesFileBasedAuthenticationManager;
import org.picketbox.authentication.impl.SimpleCredentialAuthenticationManager;
import org.picketbox.authorization.AuthorizationManager;
import org.picketbox.authorization.resource.WebResource;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.exceptions.AuthenticationException;

/**
 * A {@link Filter} that delegates to the PicketBox Security Infrastructure
 *
 * @author anil saldhana
 * @since Jul 10, 2012 
 */
public class DelegatingSecurityFilter implements Filter { 
    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private FilterConfig filterConfig;

    /**
     * Set a {@link HTTPAuthenticationScheme} from a DI/IOC environment
     *
     * @param authenticationScheme
     */
    public void setAuthenticationScheme(HTTPAuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    } 

    /**
     * Set a {@link AuthorizationManager}
     * @param authorizationManager
     */
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    } 

    @Override
    public void init(FilterConfig fc) throws ServletException {
        this.filterConfig = fc;

        ServletContext sc = filterConfig.getServletContext();
        
        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(PicketBoxConstants.SERVLET_CONTEXT, sc);
        
        //Let us try the servlet context
        String authValue = sc.getInitParameter(PicketBoxConstants.AUTHENTICATION_KEY);
        if(authValue != null && authValue.isEmpty() == false){
            //Look for auth mgr also
            String authMgrStr = sc.getInitParameter(PicketBoxConstants.AUTH_MGR);
            //Look for auth mgr also
            String authzMgrStr = sc.getInitParameter(PicketBoxConstants.AUTHZ_MGR);
            
            if (authzMgrStr != null) {
                authorizationManager =  getAuthzMgr(authzMgrStr);
                contextData.put(PicketBoxConstants.AUTHZ_MGR, authorizationManager);
            }
            
            contextData.put(PicketBoxConstants.AUTH_MGR, getAuthMgr(authMgrStr)); 
            
            authenticationScheme = getAuthenticationScheme(authValue, contextData);
        }
        else {
            String loader = filterConfig.getInitParameter(PicketBoxConstants.AUTH_SCHEME_LOADER);
            if (loader == null) {
                throw PicketBoxMessages.MESSAGES.missingRequiredInitParameter(PicketBoxConstants.AUTH_SCHEME_LOADER);
            }
            String authManagerStr = filterConfig.getInitParameter(PicketBoxConstants.AUTH_MGR);
            if (authManagerStr != null && authManagerStr.isEmpty() == false) {
                AuthenticationManager am = getAuthMgr(authManagerStr);
                contextData.put(PicketBoxConstants.AUTH_MGR, am);
            }
            String authzManagerStr = filterConfig.getInitParameter(PicketBoxConstants.AUTHZ_MGR);
            if (authzManagerStr != null && authzManagerStr.isEmpty() == false) {
                authorizationManager = getAuthzMgr(authzManagerStr);
                authorizationManager.start();
                contextData.put(PicketBoxConstants.AUTHZ_MGR, authorizationManager);
            }
            HTTPAuthenticationSchemeLoader authLoader = (HTTPAuthenticationSchemeLoader) SecurityActions.instance(getClass(),
                    loader);
            authenticationScheme = authLoader.get(contextData);   
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        Principal principal = (Principal) session.getAttribute(PicketBoxConstants.PRINCIPAL);
        if (principal == null) {
            try {
                boolean result = authenticationScheme.authenticate(request, response);
                if (result == false) {
                    return;
                }
                principal = (Principal) session.getAttribute(PicketBoxConstants.PRINCIPAL);
            } catch (AuthenticationException e) {
                throw new ServletException(e);
            }
        }
        if(principal == null){
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        //Perform Authorization
        if(authorizationManager != null){
            WebResource resource = new WebResource();
            resource.setContext(filterConfig.getServletContext());
            resource.setRequest(request);
            resource.setResponse(response);
            
            PicketBoxSubject subject = new PicketBoxSubject();
            subject.setUser(principal);
            
            boolean authorize = authorizationManager.authorize(resource, subject);
            if(!authorize){
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        if (!response.isCommitted()) {
            chain.doFilter(httpRequest, response);    
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
    
    private HTTPAuthenticationScheme getAuthenticationScheme(String value, Map<String, Object> contextData) throws ServletException{
        if(value.equals(PicketBoxConstants.BASIC)){
            return new HTTPBasicAuthenticationSchemeLoader().get(contextData);
        }
        if(value.equals(PicketBoxConstants.DIGEST)){
            return new HTTPDigestAuthenticationSchemeLoader().get(contextData);
        }
        if(value.equals(PicketBoxConstants.CLIENT_CERT)){
            return new HTTPClientCertAuthenticationSchemeLoader().get(contextData);
        }

        return new HTTPFormAuthenticationSchemeLoader().get(contextData);
    }
    
    private AuthenticationManager getAuthMgr(String value){
        if(value.equalsIgnoreCase("Credential")){
            return new SimpleCredentialAuthenticationManager();
        }
        if(value.equalsIgnoreCase("Properties")){
            return new PropertiesFileBasedAuthenticationManager();
        }
        
        if (value == null || value.isEmpty()) {
            return new PropertiesFileBasedAuthenticationManager();
        }
        
        return (AuthenticationManager) SecurityActions.instance(getClass(), value);
    }
    
    private AuthorizationManager getAuthzMgr(String value){
        if(value.equalsIgnoreCase("Drools")){
            return (AuthorizationManager) SecurityActions.instance(getClass(), "org.picketbox.drools.authorization.PicketBoxDroolsAuthorizationManager");
        } 
        
        return (AuthorizationManager) SecurityActions.instance(getClass(), "org.picketbox.drools.authorization.PicketBoxDroolsAuthorizationManager");
    }
}