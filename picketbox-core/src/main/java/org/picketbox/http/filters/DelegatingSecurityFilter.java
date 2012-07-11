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
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.picketbox.PicketBoxMessages;
import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authentication.http.HTTPAuthenticationSchemeLoader;
import org.picketbox.exceptions.AuthenticationException;

/**
 * A {@link Filter} that delegates to the PicketBox Security Infrastructure
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 *
 */
public class DelegatingSecurityFilter implements Filter {
    public static final String authenticationSchemeLoader = PicketBoxConstants.AUTH_SCHEME_LOADER;
    public static final String servletContext = PicketBoxConstants.SERVLET_CONTEXT;
    public static final String authManager = PicketBoxConstants.AUTH_MGR;

    private HTTPAuthenticationScheme authenticationScheme;
    private FilterConfig filterConfig;

    /**
     * Set a {@link HTTPAuthenticationScheme} from a DI/IOC environment
     *
     * @param authenticationScheme
     */
    public void setAuthenticationScheme(HTTPAuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        this.filterConfig = fc;

        Map<String, Object> contextData = new HashMap<String, Object>();
        contextData.put(servletContext, filterConfig.getServletContext());

        String loader = filterConfig.getInitParameter(authenticationSchemeLoader);
        if (loader == null) {
            throw PicketBoxMessages.MESSAGES.missingRequiredInitParameter(authenticationSchemeLoader);
        }
        String authManagerStr = filterConfig.getInitParameter(authManager);
        if (authManagerStr != null && authManagerStr.isEmpty() == false) {
            AuthenticationManager am = (AuthenticationManager) SecurityActions.instance(getClass(), authManagerStr);
            contextData.put(authManager, am);
        }
        HTTPAuthenticationSchemeLoader authLoader = (HTTPAuthenticationSchemeLoader) SecurityActions.instance(getClass(),
                loader);
        authenticationScheme = authLoader.get(contextData);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(true);
        Principal principal = (Principal) session.getAttribute(PicketBoxConstants.PRINCIPAL);
        if (principal == null) {
            try {
                boolean result = authenticationScheme.authenticate(request, response);
                if (result == false) {
                    return;
                }
            } catch (AuthenticationException e) {
                throw new ServletException(e);
            }
        }

        chain.doFilter(httpRequest, response);
        return;
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}