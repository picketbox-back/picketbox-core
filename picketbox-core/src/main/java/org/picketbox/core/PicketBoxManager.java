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

/**
 * <p>
 * This class acts as a <i>Facade</i> for the PicketBox Security capabilites.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class PicketBoxManager implements PicketBoxLifecycle {

    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private boolean started;
    private boolean stopped = true;
    private IdentityManager identityManager;

    PicketBoxManager() {

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
        if (!isAuthenticated(servletReq)) {
            Principal principal = this.authenticationScheme.authenticate(servletReq, servletResp);

            if (principal != null) {
                PicketBoxSubject subject = this.identityManager.getIdentity(principal);
                servletReq.getSession(true).setAttribute(PicketBoxConstants.SUBJECT, subject);
            }
        }
    }

    public PicketBoxSubject getAuthenticatedUser(HttpServletRequest servletReq) {
        return (PicketBoxSubject) servletReq.getSession().getAttribute(PicketBoxConstants.SUBJECT);
    }

    /**
     * <pAuthorizes a user.</p>
     *
     * @param servletReq
     * @param servletResp
     * @return
     * @throws AuthenticationException
     */
    public boolean authorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (this.authorizationManager == null || !this.isAuthenticated(httpRequest)) {
            return true;
        }

        WebResource resource = new WebResource();
        resource.setContext(httpRequest.getServletContext());
        resource.setRequest(httpRequest);
        resource.setResponse(httpResponse);

        return this.authorizationManager.authorize(resource, getAuthenticatedUser(httpRequest));
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
            throw new IllegalStateException("PicketBox Authorization Manager alredy started.");
        }

        PicketBoxLogger.LOGGER.debug("Using Authentication Scheme : " + this.authenticationScheme.getClass().getName());
        PicketBoxLogger.LOGGER.debug("Using Authorization Manager : " + this.authenticationScheme.getClass().getName());
        PicketBoxLogger.LOGGER.debug("Using Identity Manager : " + this.authenticationScheme.getClass().getName());
        PicketBoxLogger.LOGGER.startingPicketBox();

        this.started = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stopped()
     */
    @Override
    public boolean stopped() {
        if (this.stopped) {
            throw new IllegalStateException("PicketBox Authorization Manager alredy stopped.");
        }

        return this.stopped;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stop()
     */
    @Override
    public void stop() {
        this.started = false;
        this.stopped = true;
    }

}
