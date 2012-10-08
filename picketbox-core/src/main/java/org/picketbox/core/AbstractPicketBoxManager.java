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

import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.TrustedUsernameCredential;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.EntitlementsManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.identity.UserContextPopulator;
import org.picketbox.core.identity.impl.DefaultUserContextPopulator;
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
    private UserContextPopulator subjectPopulator;
    private IdentityManager identityManager;
    private PicketBoxConfiguration configuration;
    private PicketBoxEventManager eventManager;

    @SuppressWarnings("unused")
    //TODO: handle entitlements
    private EntitlementsManager entitlementsManager;

    public AbstractPicketBoxManager(PicketBoxConfiguration configuration) {
        this.configuration = configuration;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#authenticate(org.picketbox.core.UserContext)
     */
    @Override
    public UserContext authenticate(UserContext subject) throws AuthenticationException {
        checkIfStarted();

        PicketBoxSession userSession = restoreSession(subject);

        // if there is a valid session associate it with the subject and performs a silent authentication, trusting the provided principal.
        if (userSession != null) {
            UserContext restoredUserContext = userSession.getUserContext();
            Principal restoredPrincipal = restoredUserContext.getPrincipal(false);

            TrustedUsernameCredential credential = new TrustedUsernameCredential(restoredPrincipal.getName());

            subject = new UserContext(credential);
        }

        // performs the authentication
        performAuthentication(subject);

        if (subject.isAuthenticated()) {
            // creates a fresh new session if none was retrieved from the session manager
            if (userSession == null) {
                userSession = createSession(subject);
            }

            performSuccessfulAuthentication(subject, userSession);
        } else {
            performUnsuccessfulAuthentication(subject);
        }

        return subject;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.PicketBoxManager#logout(org.picketbox.core.UserContext)
     */
    @Override
    public void logout(UserContext authenticatedUser) throws IllegalStateException {
        checkIfStarted();

        if (authenticatedUser.isAuthenticated()) {
            authenticatedUser.invalidate();
            getEventManager().raiseEvent(new UserLoggedOutEvent());
        } else {
            throw PicketBoxMessages.MESSAGES.invalidUserSession();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxManager#authorize(org.picketbox.core.PicketBoxSecurityContext)
     */
    @Override
    public boolean authorize(UserContext subject, Resource resource) {
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
    protected boolean doPreAuthentication(UserContext subject) {
        return true;
    }

    /**
     * <p>Performs the authentication using the provided {@link Credential}.</p>
     *
     * @param subject
     * @return
     * @throws AuthenticationException
     */
    private void performAuthentication(UserContext subject) throws AuthenticationException {
        Credential credential = subject.getCredential();

        if (credential == null) {
            throw PicketBoxMessages.MESSAGES.failedToValidateCredentials();
        }

        AuthenticationResult result = null;

        if (doPreAuthentication(subject)) {
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
        }

        if (result == null) {
            result = new AuthenticationResult();
        }

        subject.setAuthenticationResult(result);
    }

    /**
     * <p>Performs some post authentication steps when the authentication is successfull.</p>
     *
     * @param session
     * @return
     */
    protected UserContext performSuccessfulAuthentication(UserContext subject, PicketBoxSession session) {
        if (!subject.isAuthenticated()) {
            throw PicketBoxMessages.MESSAGES.userNotAuthenticated();
        }

        subject.setSession(session);
        subject.setCredential(null);

        UserContext populatedUserContext = this.subjectPopulator.getIdentity(subject);

        getEventManager().raiseEvent(new UserAuthenticatedEvent(subject));

        return populatedUserContext;
    }

    /**
     * <p>Performs some post authentication steps when the authentication fail.</p>
     *
     * @param subject
     */
    protected void performUnsuccessfulAuthentication(UserContext subject) {
        getEventManager().raiseEvent(new UserAuthenticatedEvent(subject));
    }


    /**
     * <p>Tries to restore the session associated with the given {@link UserContext}.</p>
     *
     * @param subject
     * @return
     */
    private PicketBoxSession restoreSession(UserContext subject) {
        PicketBoxSession session = null;

        if (this.sessionManager != null) {
            if (subject.getSession() != null && subject.getSession().getId() != null) {
                session = this.sessionManager.retrieve(subject.getSession().getId());
            }

            // check if the provided subject is marked as authenticated and if there is a valid session
            if (subject.isAuthenticated()) {
                if (session == null || !session.isValid()) {
                    throw PicketBoxMessages.MESSAGES.invalidUserSession();
                }
            }
        }

        return session;
    }

    /**
     * <p>
     * Creates a session for the authenticated {@link UserContext}. The subject must be authenticated, its
     * isAuthenticated() method should return true.
     * </p>
     *
     * @param securityContext the security context with environment specific information
     * @param authenticatedUserContext the authenticated subject
     * @return
     *
     * @throws IllegalArgumentException in the case the subject is not authenticated.
     */
    private PicketBoxSession createSession(UserContext authenticatedUserContext) throws IllegalArgumentException {
        if (!authenticatedUserContext.isAuthenticated()) {
            throw new IllegalArgumentException("UserContext is not authenticated. Session can not be created.");
        }

        if (this.sessionManager == null) {
            return null;
        }

        return this.sessionManager.create(authenticatedUserContext);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStart()
     */
    @Override
    protected void doStart() {
        this.eventManager = this.configuration.getEventManager().getEventManager();

        if (this.configuration != null) {
            this.authenticationProvider = new PicketBoxAuthenticationProvider(this, this.configuration);

            if (!this.configuration.getAuthorization().getManagers().isEmpty()) {
                this.authorizationManager = this.configuration.getAuthorization().getManagers().get(0);
            }

            this.identityManager = new DefaultIdentityManager(this.configuration.getIdentityManager()
                    .getIdentityManagerConfiguration().getIdentityStore());

            this.subjectPopulator = this.configuration.getIdentityManager().getUserPopulator();

            if (this.subjectPopulator == null) {
                this.subjectPopulator = new DefaultUserContextPopulator(this.identityManager);
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