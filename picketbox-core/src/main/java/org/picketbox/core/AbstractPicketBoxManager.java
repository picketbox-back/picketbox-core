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
import org.picketbox.core.session.DefaultSessionManager;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;

/**
 * <p>
 * Base class for {@link PicketBoxManager} implementations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketBoxManager extends AbstractPicketBoxLifeCycle implements PicketBoxManager {

    protected AuthenticationProvider authenticationProvider;
    protected AuthorizationManager authorizationManager;
    protected SessionManager sessionManager;
    protected EntitlementsManager entitlementsManager;
    protected IdentityManager identityManager;
    protected PicketBoxConfiguration configuration;

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
            if (this.sessionManager != null) {
                this.sessionManager.remove(authenticatedUser.getSession());
            }
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
        checkIfStarted();

        PicketBoxSession session = null;

        if (this.sessionManager != null) {
            if (subject.getSession() != null && subject.getSession().getId() != null) {
                session = this.sessionManager.retrieve(subject.getSession().getId());
            }

            if (subject.isAuthenticated()) {
                if (session != null && session.isValid()) {
                    return subject;
                } else {
                    throw new IllegalArgumentException("User is authenticated, but no associated session was found or it was invalid. Session: " + session);
                }
            }
        }

        if (session != null) {
            subject = session.getSubject();
            subject.setSession(session);
        } else {
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

        if (this.sessionManager == null) {
            return;
        }

        PicketBoxSession session = this.sessionManager.create(authenticatedSubject);

        authenticatedSubject.setSession(session);
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

            if (this.authorizationManager == null || (subject == null || !subject.isAuthenticated())) {
                return true;
            }

            return this.authorizationManager.authorize(resource, subject);
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
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

            this.sessionManager = this.configuration.getSessionManager().getManager();

            if (this.sessionManager == null && this.configuration.getSessionManager().getStore() != null) {
                this.sessionManager = new DefaultSessionManager(this.configuration);
            }

            if (this.sessionManager != null) {
                this.sessionManager.start();
            }

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
        if (this.authorizationManager != null) {
            this.authorizationManager.stop();
        }

        if (this.sessionManager != null) {
            this.sessionManager.stop();
        }
    }

}