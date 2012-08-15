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
import java.util.List;

import org.picketbox.core.AbstractPicketBoxLifeCycle;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.config.PicketBoxConfiguration;

/**
 * Default implementation of the {@link SessionManager}
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class DefaultSessionManager extends AbstractPicketBoxLifeCycle implements SessionManager {

    private SessionStore sessionStore;
    private final SessionExpirationManager sessionExpirationManager;
    private final List<PicketBoxSessionListener> listeners;

    /**
     * Construct the session manager
     *
     * @param configuration PicketBox Configuration
     */
    public DefaultSessionManager(PicketBoxConfiguration configuration) {
        this.sessionExpirationManager = new SessionExpirationManager(configuration);
        this.sessionStore = configuration.getSessionManager().getStore();

        if (this.sessionStore == null) {
            this.sessionStore = new InMemorySessionStore();
        }

        this.listeners = configuration.getSessionManager().getListeners();
        this.listeners.add(new PicketBoxSessionStoreListener(this));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.SessionManager#create(org.picketbox.core.PicketBoxSubject)
     */
    @Override
    public PicketBoxSession create(PicketBoxSubject authenticatedSubject) {
        PicketBoxSession session = doCreateSession(authenticatedSubject);

        for (PicketBoxSessionListener listener : this.listeners) {
            session.addListener(listener);
            listener.onCreate(session);
        }

        if (session.getId() == null || session.getId().getId() == null) {
            throw new IllegalStateException("Invalid session id: " + session.getId());
        }

        // checks for duplicate session id
        if (this.sessionStore.load(session.getId()) != null) {
            throw new IllegalStateException("Duplicate session id: " + session.getId());
        }

        authenticatedSubject.setSession(session);

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
        PicketBoxSession session = this.sessionStore.load(id);

        if (session != null && !session.hasListener(PicketBoxSessionStoreListener.class)) {
            session.addListener(new PicketBoxSessionStoreListener(this));
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
        if (session != null) {
            this.sessionStore.remove(session.getId());
        }
    }

    @Override
    public void update(PicketBoxSession session) {
        this.sessionStore.update(session);
    }

    protected PicketBoxSession doCreateSession(PicketBoxSubject authenticatedSubject) {
        return new PicketBoxSession(authenticatedSubject, new DefaultSessionId());
    }

    @Override
    protected void doStart() {
        this.sessionStore.start();
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.AbstractPicketBoxLifeCycle#doStop()
     */
    @Override
    protected void doStop() {
        this.sessionStore.stop();
    }
}