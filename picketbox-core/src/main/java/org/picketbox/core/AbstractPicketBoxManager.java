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

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationService;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.EntitlementsManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.logout.LogoutManager;
import org.picketbox.core.resource.ProtectedResource;
import org.picketbox.core.resource.ProtectedResourceManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketBoxManager extends AbstractPicketBoxLifeCycle implements PicketBoxManager {

    private AuthenticationProvider authenticationProvider;
    private LogoutManager logoutManager;
    private AuthorizationManager authorizationManager;
    private ProtectedResourceManager protectedResourceManager;
    private EntitlementsManager entitlementsManager;
    private IdentityManager identityManager;

    /**
     * @param authenticationCallbackHandler
     * @throws AuthenticationException
     */
    public PicketBoxSubject authenticate(AuthenticationCallbackHandler authenticationCallbackHandler)
            throws AuthenticationException {
        AuthenticationResult result = null;

        String[] mechanisms = this.authenticationProvider.getSupportedMechanisms();

        for (String mechanismName : mechanisms) {
            AuthenticationMechanism mechanism = this.authenticationProvider.getMechanism(mechanismName);
            AuthenticationService authenticationService = mechanism.getService();

            if (authenticationService.supportsHandler(authenticationCallbackHandler.getClass())) {
                try {
                    result = authenticationService.authenticate(authenticationCallbackHandler);
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }
            }
        }

        if (result == null) {
            throw new AuthenticationException("Authentication not supported. Using handler: " + authenticationCallbackHandler);
        }

        PicketBoxSubject subject = null;

        if (result.getStatus().equals(AuthenticationStatus.SUCCESS)) {
            subject = this.identityManager.getIdentity(result.getSubject().getUser());
        }

        return subject;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSecurityContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean authorize(PicketBoxSubject subject, Resource resource) {
        try {
            checkIfStarted();

            if (this.protectedResourceManager != null) {
                ProtectedResource protectedResource = this.protectedResourceManager.getProtectedResource(resource);

                if (!protectedResource.requiresAuthorization()) {
                    return true;
                }
            }

            if (this.authorizationManager == null) {
                return true;
            }

            boolean isAuthorized = this.authorizationManager.authorize(resource, subject);

            return isAuthorized;
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
    }

    public ProtectedResource getProtectedResource(Resource resource) {
        if (this.protectedResourceManager == null) {
            return ProtectedResource.DEFAULT_RESOURCE;
        }

        return this.protectedResourceManager.getProtectedResource(resource);
    }

    public void setProtectedResourceManager(ProtectedResourceManager protectedResourceManager) {
        this.protectedResourceManager = protectedResourceManager;
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

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStart()
     */
    @Override
    protected void doStart() {
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

        if (this.protectedResourceManager != null) {
            this.protectedResourceManager.start();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStop()
     */
    @Override
    protected void doStop() {

    }

    public AuthenticationProvider getAuthenticationProvider() {
        return this.authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider build) {
        this.authenticationProvider = build;
    }

}
