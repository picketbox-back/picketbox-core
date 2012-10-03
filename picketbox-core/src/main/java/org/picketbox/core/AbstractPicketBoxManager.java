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

import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.EntitlementsManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.identity.PicketBoxSubjectPopulator;
import org.picketbox.core.identity.impl.DefaultSubjectPopulator;
import org.picketbox.core.logout.UserLoggedOutEvent;
import org.picketbox.core.session.DefaultSessionManager;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.internal.DefaultIdentityManager;

/**
 * <p>
 * Base class for {@link PicketBoxManager} implementations.
 * </p>
 *
 * @author anil saldhana
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractPicketBoxManager extends AbstractPicketBoxLifeCycle implements PicketBoxManager {

    private AuthenticationProvider authenticationProvider;
    private AuthorizationManager authorizationManager;
    private SessionManager sessionManager;
    private EntitlementsManager entitlementsManager; // TODO: handle entitlements
    private PicketBoxSubjectPopulator subjectPopulator;
    private IdentityManager identityManager;
    private PicketBoxConfiguration configuration;
    private PicketBoxEventManager eventManager;

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
        checkIfStarted();

        if (authenticatedUser.isAuthenticated()) {
            authenticatedUser.invalidate();
            getEventManager().raiseEvent(new UserLoggedOutEvent());
        } else {
            throw PicketBoxMessages.MESSAGES.invalidUserSession();
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#authenticate(org.picketbox.core.PicketBoxSubject)
     */
    public PicketBoxSubject authenticate(PicketBoxSubject subject) throws AuthenticationException {
        checkIfStarted();

        PicketBoxSession session = null;

        if (this.sessionManager != null) {
            if (subject.getSession() != null && subject.getSession().getId() != null) {
                session = this.sessionManager.retrieve(subject.getSession().getId());
            }

            if (subject.isAuthenticated()) {
                if (session == null || !session.isValid()) {
                    throw PicketBoxMessages.MESSAGES.invalidUserSession();
                }
            }
        }

        // if there is a valid session associate with the subject and performs a silent authentication.
        if (session != null) {
            AuthenticationResult result = new AuthenticationResult();

            result.setStatus(AuthenticationStatus.SUCCESS);

            getEventManager().raiseEvent(new UserAuthenticatedEvent(result));

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
                    subject.setPrincipal(result.getPrincipal());

                    subject = this.subjectPopulator.getIdentity(subject);

                    subject.setCredential(null);

                    createSession(subject);

                    getEventManager().raiseEvent(new UserAuthenticatedEvent(result));
                } else {
                    getEventManager().raiseEvent(new UserAuthenticatedEvent(result));
                }
            }
        }

        return subject;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSecurityContext)
     */
    @Override
    public boolean authorize(PicketBoxSubject subject, Resource resource) {
        checkIfStarted();
        try {
            if (this.authorizationManager == null || (subject == null || !subject.isAuthenticated())) {
                return true;
            }

            return this.authorizationManager.authorize(resource, subject);
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.authorizationFailed(e);
        }
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
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStart()
     */
    @Override
    protected void doStart() {
        this.eventManager = configuration.getEventManager().getEventManager();

        if (this.configuration != null) {
            this.authenticationProvider = new PicketBoxAuthenticationProvider(this, this.configuration);

            if (!this.configuration.getAuthorization().getManagers().isEmpty()) {
                this.authorizationManager = this.configuration.getAuthorization().getManagers().get(0);
            }

            this.identityManager = new DefaultIdentityManager(this.configuration.getIdentityManager()
                    .getIdentityManagerConfiguration().getIdentityStore());

            this.subjectPopulator = this.configuration.getIdentityManager().getUserPopulator();

            if (this.subjectPopulator == null) {
                this.subjectPopulator = new DefaultSubjectPopulator(this.identityManager);
            }

            this.sessionManager = this.configuration.getSessionManager().getManager();

            if (this.sessionManager == null && this.configuration.getSessionManager().getStore() != null) {
                this.sessionManager = new DefaultSessionManager(this);
            }

            if (this.sessionManager != null) {
                this.sessionManager.start();
            }

            doConfigure();
        }

        if (this.authorizationManager != null) {
            PicketBoxLogger.LOGGER.debug("Using Authorization Manager : " + this.authorizationManager.getClass().getName());
        }

        if (this.subjectPopulator != null) {
            PicketBoxLogger.LOGGER.debug("Using Identity Manager : " + this.subjectPopulator.getClass().getName());
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

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#getEventManager()
     */
    @Override
    public PicketBoxEventManager getEventManager() {
        return this.eventManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#getIdentityManager()
     */
    @Override
    public IdentityManager getIdentityManager() {
        return this.identityManager;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#getConfiguration()
     */
    @Override
    public PicketBoxConfiguration getConfiguration() {
        return this.configuration;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#getSessionManager()
     */
    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    protected void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

}