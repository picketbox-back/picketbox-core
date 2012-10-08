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

package org.picketbox.core.session;

import java.io.Serializable;

import org.picketbox.core.AbstractPicketBoxLifeCycle;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.session.event.SessionEvent;
import org.picketbox.core.session.event.SessionEventHandler;

/**
 * Default implementation of the {@link SessionManager}
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class DefaultSessionManager extends AbstractPicketBoxLifeCycle implements SessionManager {

    private SessionStore sessionStore;
    private final SessionExpirationManager sessionExpirationManager;
    private PicketBoxManager picketBoxManager;
    private PicketBoxEventHandler defaultSessionEventHandler = new DefaultSessionEventHandler(this);

    /**
     * Construct the session manager
     *
     * @param configuration PicketBox Configuration
     */
    public DefaultSessionManager(PicketBoxManager picketBoxManager) {
        this.picketBoxManager = picketBoxManager;

        PicketBoxConfiguration configuration = this.picketBoxManager.getConfiguration();

        this.sessionExpirationManager = new SessionExpirationManager(configuration);
        this.sessionStore = configuration.getSessionManager().getStore();

        if (this.sessionStore == null) {
            this.sessionStore = new InMemorySessionStore();
        }

        registerDefaultEventHandler();
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.session.SessionManager#create(org.picketbox.core.UserContext)
     */
    @Override
    public PicketBoxSession create(UserContext authenticatedUserContext) {
        checkIfStarted();

        PicketBoxSession session = doCreateSession(authenticatedUserContext);

        session.setEventManager(this.picketBoxManager.getEventManager());

        fireEvent(new SessionEvent(session) {
            @Override
            public void dispatch(SessionEventHandler handler) {
                handler.onCreate(this);
            }
        });

        if (session.getId() == null || session.getId().getId() == null) {
            throw new IllegalStateException("Invalid session id: " + session.getId());
        }

        // checks for duplicate session id
        if (this.sessionStore.load(session.getId()) != null) {
            throw new IllegalStateException("Duplicate session id: " + session.getId());
        }

        authenticatedUserContext.setSession(session);

        this.sessionStore.store(session);

        this.sessionExpirationManager.setTimer(session);

        return session;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionManager#retrieve(org.picketbox.core.session.SessionId)
     */
    @Override
    public PicketBoxSession retrieve(SessionId<? extends Serializable> id) {
        checkIfStarted();

        PicketBoxSession session = this.sessionStore.load(id);

        if (session != null) {
            session.setEventManager(this.picketBoxManager.getEventManager());
        }

        return session;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionManager#remove(org.picketbox.core.session.PicketBoxSession)
     */
    @Override
    public void remove(PicketBoxSession session) {
        checkIfStarted();

        if (session != null) {
            this.sessionStore.remove(session.getId());
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.session.SessionManager#update(org.picketbox.core.session.PicketBoxSession)
     */
    @Override
    public void update(PicketBoxSession session) {
        checkIfStarted();

        this.sessionStore.update(session);
    }

    protected PicketBoxSession doCreateSession(UserContext authenticatedUserContext) {
        return new PicketBoxSession(authenticatedUserContext, new DefaultSessionId(), this.picketBoxManager.getEventManager());
    }

    @Override
    protected void doStart() {
        this.sessionStore.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStop()
     */
    @Override
    protected void doStop() {
        this.sessionStore.stop();
    }

    /**
     * <p>Fires the specified {@link SessionEvent}.</p>
     *
     * @param event
     */
    protected void fireEvent(SessionEvent event) {
        this.picketBoxManager.getEventManager().raiseEvent(event);
    }

    /**
     * <p>Registers the default implementation for {@link SessionEventHandler}.</p>
     */
    private void registerDefaultEventHandler() {
        this.picketBoxManager.getEventManager().addHandler(this.defaultSessionEventHandler );
    }

    protected PicketBoxManager getPicketBoxManager() {
        return picketBoxManager;
    }
}