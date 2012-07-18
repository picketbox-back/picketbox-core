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

package org.picketbox.core;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.EntitlementsManager;
import org.picketbox.core.authorization.resource.WebResource;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.AuthorizationException;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.logout.LogoutManager;
import org.picketbox.core.resource.ProtectedResource;
import org.picketbox.core.resource.ProtectedResourceManager;

/**
 * <p>
 * This class acts as a <i>Facade</i> for the PicketBox Security capabilites.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public final class PicketBoxManager extends AbstractPicketBoxLifeCycle {

    private HTTPAuthenticationScheme authenticationScheme;
    private LogoutManager logoutManager;
    private ProtectedResourceManager protectedResourceManager;
    private AuthorizationManager authorizationManager;
    private EntitlementsManager entitlementsManager;
    private IdentityManager identityManager;

    PicketBoxManager(HTTPAuthenticationScheme authenticationScheme, LogoutManager logoutManager,
            ProtectedResourceManager protectedResourceManager) {
        if (authenticationScheme == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("Authentication Scheme");
        }

        if (logoutManager == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("Logout Manager");
        }

        if (protectedResourceManager == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("Protected Resource Manager");
        }

        this.authenticationScheme = authenticationScheme;
        this.logoutManager = logoutManager;
        this.protectedResourceManager = protectedResourceManager;
    }

    /**
     * <p>
     * Checks if the specified {@link HttpServletRequest} instance is from an authenticated user.
     * </p>
     *
     * @param servletReq
     * @return true if the request came from an authenticated user.
     */
    public boolean isAuthenticated(HttpServletRequest servletReq) {
        return getAuthenticatedUser(servletReq) != null;
    }

    /**
     * <p>
     * Authenticates a user.
     * </p>
     *
     * @param servletReq
     * @param servletResp
     * @throws AuthenticationException
     */
    public void authenticate(HttpServletRequest servletReq, HttpServletResponse servletResp) throws AuthenticationException {
        checkIfStarted();

        ProtectedResource protectedResource = getProtectedResource(servletReq);

        if (protectedResource.requiresAuthentication()) {
            if (!isAuthenticated(servletReq)) {
                Principal principal = this.authenticationScheme.authenticate(servletReq, servletResp);

                if (principal != null) {
                    PicketBoxSubject subject = this.identityManager.getIdentity(principal);
                    servletReq.getSession(true).setAttribute(PicketBoxConstants.SUBJECT, subject);
                }
            }
        }
    }

    public PicketBoxSubject getAuthenticatedUser(HttpServletRequest servletReq) {
        checkIfStarted();

        if (servletReq.getSession(false) == null) {
            return null;
        }

        return (PicketBoxSubject) servletReq.getSession(false).getAttribute(PicketBoxConstants.SUBJECT);
    }

    /**
     * <pAuthorizes a user.</p>
     *
     * @param servletReq
     * @param servletResp
     *
     * @return true is the user is authorized.
     *
     * @throws AuthorizationException if some problem occurs during the authorization process.
     */
    public boolean authorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthorizationException {
        try {
            checkIfStarted();

            ProtectedResource protectedResource = getProtectedResource(httpRequest);

            if (!isPerformAuthorization(httpRequest, protectedResource)) {
                return true;
            }

            WebResource resource = new WebResource();
            resource.setContext(httpRequest.getServletContext());
            resource.setRequest(httpRequest);
            resource.setResponse(httpResponse);

            boolean isAuthorized = this.authorizationManager.authorize(resource, getAuthenticatedUser(httpRequest));

            return isAuthorized;
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        checkIfStarted();
        this.logoutManager.logout(request, response);
    }

    private boolean isPerformAuthorization(HttpServletRequest httpRequest, ProtectedResource protectedResource) {
        return this.authorizationManager != null && this.isAuthenticated(httpRequest) && protectedResource.requiresAuthorization();
    }

    /**
     * @return the authenticationScheme
     */
    public HTTPAuthenticationScheme getAuthenticationScheme() {
        return authenticationScheme;
    }

    /**
     * @param authenticationScheme the authenticationScheme to set
     */
    public void setAuthenticationScheme(HTTPAuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    /**
     * @return the authorizationManager
     */
    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    /**
     * @param authorizationManager the authorizationManager to set
     */
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    /**
     * @return the identityManager
     */
    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    /**
     * @param identityManager the identityManager to set
     */
    public void setIdentityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    /**
     * @return the logoutManager
     */
    public LogoutManager getLogoutManager() {
        return this.logoutManager;
    }

    /**
     * @param logoutManager the logoutManager to set
     */
    public void setLogoutManager(LogoutManager logoutManager) {
        this.logoutManager = logoutManager;
    }

    /**
     * Get the {@link EntitlementsManager}
     *
     * @return
     */
    public EntitlementsManager getEntitlementsManager() {
        return entitlementsManager;
    }

    /**
     * Set the {@link EntitlementsManager}
     *
     * @param entitlementsManager
     */
    public void setEntitlementsManager(EntitlementsManager entitlementsManager) {
        this.entitlementsManager = entitlementsManager;
    }

    /**
     * Get the {@link ProtectedResourceManager}
     *
     * @return the protectedResourceManager
     */
    public ProtectedResourceManager getProtectedResourceManager() {
        return protectedResourceManager;
    }

    /**
     * Set the {@link ProtectedResourceManager}
     *
     * @param protectedResourceManager
     */
    public void setProtectedResourceManager(ProtectedResourceManager protectedResourceManager) {
        this.protectedResourceManager = protectedResourceManager;
    }

    private ProtectedResource getProtectedResource(HttpServletRequest servletReq) {
        return this.protectedResourceManager.getProtectedResource(servletReq);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStart()
     */
    @Override
    protected void doStart() {
        PicketBoxLogger.LOGGER.debug("Using Authentication Scheme : " + this.authenticationScheme.getClass().getName());
        PicketBoxLogger.LOGGER.debug("Using Logout Manager : " + this.logoutManager.getClass().getName());
        PicketBoxLogger.LOGGER
                .debug("Using Protected Resource Manager : " + this.protectedResourceManager.getClass().getName());

        if (this.authorizationManager != null) {
            PicketBoxLogger.LOGGER.debug("Using Authorization Manager : " + this.authorizationManager.getClass().getName());
        }

        if (this.identityManager != null) {
            PicketBoxLogger.LOGGER.debug("Using Identity Manager : " + this.identityManager.getClass().getName());
        }

        PicketBoxLogger.LOGGER.startingPicketBox();

        if (this.authorizationManager != null) {
            this.authorizationManager.start();
        }

        this.protectedResourceManager.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStop()
     */
    @Override
    protected void doStop() {

    }

}