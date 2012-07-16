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
package org.picketbox.core.authentication.http;

import static org.picketbox.core.PicketBoxMessages.MESSAGES;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.Base64;

/**
 * Perform HTTP Form Authentication
 *
 * @author anil saldhana
 * @since July 9, 2012
 */
public class HTTPFormAuthentication extends AbstractHTTPAuthentication {

    private static final String DEFAULT_PAGE_URL = "/";

    private RequestCache requestCache = new RequestCache();

    /**
     * The page used to redirect the user after a succesful authentication.
     */
    protected String defaultPage = DEFAULT_PAGE_URL;

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

    /**
     * The default page. It should always start with a '/'
     */
    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }

    /**
     * Authenticate an user
     *
     * @param servletReq
     * @param servletResp
     * @return
     * @throws AuthenticationException
     */
    public Principal authenticate(ServletRequest servletReq, ServletResponse servletResp) throws AuthenticationException {
        String username, password;

        HttpServletRequest request = (HttpServletRequest) servletReq;
        HttpServletResponse response = (HttpServletResponse) servletResp;
        HttpSession session = request.getSession(true);

        boolean jSecurityCheck = request.getRequestURI().contains(PicketBoxConstants.HTTP_FORM_J_SECURITY_CHECK);

        username = request.getParameter(PicketBoxConstants.HTTP_FORM_J_USERNAME);
        password = request.getParameter(PicketBoxConstants.HTTP_FORM_J_PASSWORD);

        if (jSecurityCheck == false && principalExists(session) == false) {
            challengeClient(request, response);
            return null;
        }

        if (username != null && password != null) {
            if (authManager == null) {
                throw MESSAGES.invalidNullAuthenticationManager();
            }

            Principal principal = authManager.authenticate(username, password);

            if (principal != null) {
                // remove from the cache the saved request and store it in the session for further use.
                String savedRequest = this.requestCache.removeAndStoreSavedRequestInSession(request).getRequestURI();

                // if the user has explicit defined a default page url, use it to redirect the user after a successful authentication.
                if (!this.defaultPage.equals(DEFAULT_PAGE_URL)) {
                    sendRedirect(response, request.getContextPath() + this.defaultPage);
                } else {
                    sendRedirect(response, savedRequest);
                }

                return principal;
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
                            throw MESSAGES.invalidNullAuthenticationManager();
                        }

                        return authManager.authenticate(username, password);
                    }
                }
            }
        }

        challengeClient(request, response);

        return null;
    }

    protected void sendRedirect(HttpServletResponse response, String redirectUrl) throws AuthenticationException {
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw MESSAGES.failRedirectToDefaultPage(redirectUrl, e);
        }
    }

    private boolean principalExists(HttpSession session) {
        return session.getAttribute(PicketBoxConstants.PRINCIPAL) != null;
    }

    private boolean challengeClient(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (servletContext == null)
            throw MESSAGES.invalidNullServletContext();

        this.requestCache.saveRequest(request);
        forwardRequest(request, response, formAuthPage);

        return false;
    }

    protected void forwardRequest(HttpServletRequest request, HttpServletResponse response, String formAuthPage2)
            throws AuthenticationException {
        RequestDispatcher rd = servletContext.getRequestDispatcher(formAuthPage2);
        if (rd == null)
            throw MESSAGES.unableToFindRequestDispatcher();

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
    }
}