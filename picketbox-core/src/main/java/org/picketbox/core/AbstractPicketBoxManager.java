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

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.EntitlementsManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.session.PicketBoxSession;

/**
 * <p>
 * Base class for {@link PicketBoxManager} implementations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketBoxManager extends AbstractPicketBoxLifeCycle implements PicketBoxManager {

    private AuthenticationProvider authenticationProvider;
    private AuthorizationManager authorizationManager;
    private EntitlementsManager entitlementsManager;
    private IdentityManager identityManager;
    private PicketBoxConfiguration configuration;

    public AbstractPicketBoxManager(PicketBoxConfiguration configuration) {
        this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#logout(org.picketbox.core.PicketBoxSubject)
     */
    @Override
    public void logout(PicketBoxSubject authenticatedUser) throws IllegalStateException {
        if (authenticatedUser.isAuthenticated()) {
            authenticatedUser.getSession().expire();
            authenticatedUser.setAuthenticated(false);
        } else {
            throw PicketBoxMessages.MESSAGES.invalidUserSession();
        }
    }

    /**
     * @param authenticationCallbackHandler
     * @throws AuthenticationException
     */
    public PicketBoxSubject authenticate(PicketBoxSubject subject) throws AuthenticationException {
        Credential credential = subject.getCredential();

        if (credential == null) {
            throw PicketBoxMessages.MESSAGES.failedToValidateCredentials();
        }

        if (doPreAuthentication(subject)) {
            AuthenticationResult result = null;

            String[] mechanisms = this.authenticationProvider.getSupportedMechanisms();

            for (String mechanismName : mechanisms) {
                AuthenticationMechanism mechanism = this.authenticationProvider.getMechanism(mechanismName);

                if (mechanism.supports(credential)) {
                    try {
                        result = mechanism.authenticate(credential);
                    } catch (AuthenticationException e) {
                        throw PicketBoxMessages.MESSAGES.authenticationFailed(e);
                    }
                }
            }

            if (result == null) {
                throw PicketBoxMessages.MESSAGES.failedToValidateCredentials();
            }

            subject.setAuthenticated(result.getStatus().equals(AuthenticationStatus.SUCCESS));

            if (subject.isAuthenticated()) {
                subject.setUser(result.getPrincipal());

                this.identityManager.getIdentity(subject);

                subject.setCredential(null);

                createSession(subject);
            }
        }

        return subject;
    }

    /**
     * @param securityContext
     * @param authenticationCallbackHandler
     * @return
     */
    protected boolean doPreAuthentication(PicketBoxSubject subject) {
        return true;
    }

    /**
     * <p>
     * Creates a session for the authenticated {@link PicketBoxSubject}. The subject must be authenticated, its
     * isAuthenticated() method should return true.
     * </p>
     *
     * @param securityContext the security context with environment specific information
     * @param authenticatedSubject the authenticated subject
     *
     * @throws IllegalArgumentException in the case the subject is not authenticated.
     */
    private void createSession(PicketBoxSubject authenticatedSubject) throws IllegalArgumentException {
        if (!authenticatedSubject.isAuthenticated()) {
            throw new IllegalArgumentException("Subject is not authenticated. Session can not be created.");
        }

        PicketBoxSession session = doCreateSession(authenticatedSubject);

        if (session != null) {
            authenticatedSubject.setSession(session);
        }
    }

    /**
     * <p>
     * Subclasses should override this method to implement how {@link PicketBoxSession} are created.
     * </p>
     *
     * @param securityContext the security context with environment specific information
     * @param authenticatedSubject the authenticated subject
     *
     * @return
     */
    protected PicketBoxSession doCreateSession(PicketBoxSubject resultingSubject) {
        return new PicketBoxSession();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSecurityContext)
     */
    @Override
    public boolean authorize(PicketBoxSubject subject, Resource resource) {
        try {
            checkIfStarted();

            if (this.authorizationManager == null || !subject.isAuthenticated()) {
                return true;
            }

            return this.authorizationManager.authorize(resource, subject);
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
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
        if (this.configuration != null) {
            this.authenticationProvider = new PicketBoxAuthenticationProvider(this.configuration);

            for (AuthenticationManager authManager : this.configuration.getAuthentication().getAuthManagers()) {
                this.authenticationProvider.addAuthManager(authManager);
            }

            if (!this.configuration.getAuthorization().getManagers().isEmpty()) {
                this.authorizationManager = this.configuration.getAuthorization().getManagers().get(0);
            }

            this.identityManager = this.configuration.getIdentityManager().getManagers().get(0);

            doConfigure();
        }

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
    }

    protected void doConfigure() {

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