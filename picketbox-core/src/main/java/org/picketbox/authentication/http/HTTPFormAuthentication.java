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
package org.picketbox.authentication.http;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.util.Base64;

/**
 * Perform HTTP Form Authentication
 * 
 * @author anil saldhana
 * @since July 9, 2012
 */
public class HTTPFormAuthentication extends AbstractHTTPAuthentication {

    /**
     * The FORM login page. It should always start with a '/'
     */
    protected String formAuthPage = "/login.jsp";

    /**
     * The FORM error page. It should always start with a '/'
     */
    protected String formErrorPage = "/error.jsp";

    /**
     * The FORM login page. It should always start with a '/'
     */
    public void setFormAuthPage(String formAuthPage) {
        this.formAuthPage = formAuthPage;
    }

    /**
     * The FORM error page. It should always start with a '/'
     */
    public void setFormErrorPage(String formErrorPage) {
        this.formErrorPage = formErrorPage;
    }

    private ConcurrentMap<String, HttpServletRequest> requestcache = new ConcurrentHashMap<String, HttpServletRequest>();

    /**
     * Authenticate an user
     * 
     * @param servletReq
     * @param servletResp
     * @return
     * @throws AuthenticationException
     */
    public boolean authenticate(ServletRequest servletReq, ServletResponse servletResp) throws AuthenticationException {
        String username, password;

        HttpServletRequest request = (HttpServletRequest) servletReq;
        HttpServletResponse response = (HttpServletResponse) servletResp;
        HttpSession session = request.getSession(true);

        String sessionId = session.getId();

        boolean jSecurityCheck = request.getRequestURI().contains(PicketBoxConstants.HTTP_FORM_J_SECURITY_CHECK);

        username = request.getParameter(PicketBoxConstants.HTTP_FORM_J_USERNAME);
        password = request.getParameter(PicketBoxConstants.HTTP_FORM_J_PASSWORD);

        if (jSecurityCheck == false && principalExists(session) == false) {
            saveRequest(sessionId, request);
            challengeClient(request, response);
            return false;
        }

        if (username != null && password != null) {
            if (authManager == null) {
                throw new AuthenticationException("Auth Manager is not injected");
            }

            Principal principal = authManager.authenticate(username, password);
            if (principal != null) {
                session.setAttribute(PicketBoxConstants.PRINCIPAL, principal);
                restoreRequest(sessionId, response);
                return true;
            }
        }

        // Get the Authorization Header
        String authorizationHeader = request.getHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER);

        if (authorizationHeader != null && authorizationHeader.isEmpty() == false) {

            int whitespaceIndex = authorizationHeader.indexOf(' ');

            if (whitespaceIndex > 0) {
                String method = authorizationHeader.substring(0, whitespaceIndex);

                if (PicketBoxConstants.HTTP_BASIC.equalsIgnoreCase(method)) {
                    authorizationHeader = authorizationHeader.substring(whitespaceIndex + 1);
                    authorizationHeader = new String(Base64.decode(authorizationHeader));
                    int indexOfColon = authorizationHeader.indexOf(':');
                    if (indexOfColon > 0) {
                        username = authorizationHeader.substring(0, indexOfColon);
                        password = authorizationHeader.substring(indexOfColon + 1);

                        if (authManager == null) {
                            throw new AuthenticationException("Auth Manager is not injected");
                        }

                        Principal principal = authManager.authenticate(username, password);
                        if (principal != null) {
                            session.setAttribute(PicketBoxConstants.PRINCIPAL, principal);
                            return true;
                        }
                    }
                }
            }
        }

        // Save the original request if not already present
        saveRequest(sessionId, request);
        return challengeClient(request, response);
    }

    private void saveRequest(String id, HttpServletRequest request) {
        if (requestcache.get(id) == null) {
            requestcache.put(id, request);
        }
    }

    private void restoreRequest(String id, HttpServletResponse response) throws AuthenticationException {
        HttpServletRequest request = requestcache.remove(id);
        if (request == null)
            throw new AuthenticationException("Unable to forward to cached request");

        try {
            response.sendRedirect(request.getRequestURI());
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }

    private boolean principalExists(HttpSession session) {
        return session.getAttribute(PicketBoxConstants.PRINCIPAL) != null;
    }

    private boolean challengeClient(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (servletContext == null)
            throw new AuthenticationException("Servlet Context is not injected");

        RequestDispatcher rd = servletContext.getRequestDispatcher(formAuthPage);
        if (rd == null)
            throw new AuthenticationException("Request Dispatcher could not be found");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
        return false;
    }
}