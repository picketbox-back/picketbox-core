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
import org.picketbox.core.authorization.resource.WebResource;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.AuthorizationException;
import org.picketbox.core.logout.LogoutManager;

/**
 * <p>
 * This class acts as a <i>Facade</i> for the PicketBox Security capabilites.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public final class PicketBoxManager implements PicketBoxLifecycle {

    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private IdentityManager identityManager;
    private LogoutManager logoutManager;

    /*
     * Life cycle attributes.
     */
    private boolean started;
    private boolean stopped = true;

    PicketBoxManager(HTTPAuthenticationScheme authenticationScheme, LogoutManager logoutManager) {
        if (authenticationScheme == null) {
            throw PicketBoxMessages.MESSAGES.authenticationSchemeNotProvided();
        }

        this.authenticationScheme = authenticationScheme;

        this.logoutManager = logoutManager;
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
        if (!isAuthenticated(servletReq)) {
            Principal principal = this.authenticationScheme.authenticate(servletReq, servletResp);

            if (principal != null) {
                PicketBoxSubject subject = this.identityManager.getIdentity(principal);
                servletReq.getSession(true).setAttribute(PicketBoxConstants.SUBJECT, subject);
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

            if (this.authorizationManager == null || !this.isAuthenticated(httpRequest)) {
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

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#started()
     */
    @Override
    public boolean started() {
        return this.started;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#start()
     */
    @Override
    public void start() {
        if (this.started) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerAlreadyStarted();
        }

        PicketBoxLogger.LOGGER.debug("Using Authentication Scheme : " + this.authenticationScheme.getClass().getName());
        PicketBoxLogger.LOGGER.debug("Using Logout Manager : " + this.logoutManager.getClass().getName());

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

        this.started = true;
        this.stopped = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stopped()
     */
    @Override
    public boolean stopped() {
        return this.stopped;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stop()
     */
    @Override
    public void stop() {
        if (this.stopped) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerAlreadyStopped();
        }

        this.started = false;
        this.stopped = true;
    }

    /**
     * <p>
     * Checks if the manager is started.
     * </p>
     */
    private void checkIfStarted() {
        if (!this.started()) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerNotStarted();
        }
    }

}
