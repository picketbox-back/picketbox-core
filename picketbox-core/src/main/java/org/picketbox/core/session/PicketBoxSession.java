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
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.session.event.SessionEvent;
import org.picketbox.core.session.event.SessionEventHandler;

/**
 * A session that is capable of storing attributes
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class PicketBoxSession implements Serializable {

    private static final long serialVersionUID = 2149908831443524877L;

    protected ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    protected SessionId<? extends Serializable> id;

    protected boolean invalid = false;

    protected PicketBoxSubject subject;

    private transient PicketBoxEventManager eventManager;

    public PicketBoxSession() {
        this(new DefaultSessionId());
    }

    public PicketBoxSession(PicketBoxSubject subject, SessionId<? extends Serializable> id, PicketBoxEventManager eventManager) {
        this(id);
        this.subject = subject;
        this.eventManager = eventManager;
    }

    /**
     * Usable by {@link PicketBoxSessionManager#create()}
     */
    public PicketBoxSession(SessionId<? extends Serializable> id) {
        this.id = id;
    }

    /**
     * Get the session id
     *
     * @return
     */
    public SessionId<? extends Serializable> getId() {
        return id;
    }

    /**
     *
     * @param key
     * @throws PicketBoxSessionException
     */
    public void removeAttribute(String key) throws PicketBoxSessionException {
        checkIfIsInvalid();
        attributes.remove(key);
    }

    /**
     * Add an attribute
     *
     * @param key
     * @param val
     * @throws PicketBoxSessionException
     */
    public void setAttribute(final String key, final Object val) throws PicketBoxSessionException {
        checkIfIsInvalid();

        attributes.put(key, val);

        this.eventManager.raiseEvent(new SessionEvent(this) {
            @Override
            public void dispatch(SessionEventHandler handler) {
                handler.onSetAttribute(this, key, val);
            }
        });
    }

    /**
     * Get a read only copy of the attributes
     *
     * @return
     * @throws PicketBoxSessionException
     */
    public Map<String, Object> getAttributes() throws PicketBoxSessionException {
        checkIfIsInvalid();
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Get an attribute
     *
     * @param key
     * @return
     * @throws PicketBoxSessionException
     */
    public Object getAttribute(final String key) throws PicketBoxSessionException {
        checkIfIsInvalid();

        this.eventManager.raiseEvent(new SessionEvent(this) {
            @Override
            public void dispatch(SessionEventHandler handler) {
                handler.onGetAttribute(this, key);
            }
        });

        return attributes.get(key);
    }

    /**
     * Is the session valid?
     *
     * @return
     */
    public boolean isValid() {
        return !invalid;
    }

    /**
     * Invalidate the session and notify the registered {@link PicketBoxSessionListener}.
     *
     * @throws PicketBoxSessionException
     */
    public void invalidate() throws PicketBoxSessionException {
        invalidate(true);
    }

    /**
     * <p>
     * Invalidate the session and notigy the registered {@link PicketBoxSessionListener} only if the specified argument is true.
     * </p>
     *
     * @param raiseEvent
     * @throws PicketBoxSessionException
     */
    public void invalidate(boolean raiseEvent) throws PicketBoxSessionException {
        checkIfIsInvalid();
        if (raiseEvent) {
            this.eventManager.raiseEvent(new SessionEvent(this) {
                @Override
                public void dispatch(SessionEventHandler handler) {
                    handler.onInvalidate(this);
                }
            });
        }
        this.attributes.clear();
        this.invalid = true;
        if (this.subject != null && this.subject.isAuthenticated()) {
            this.subject.invalidate();
        }
    }

    /**
     * Expire the session
     *
     * @throws PicketBoxSessionException
     */
    public void expire() throws PicketBoxSessionException {
        invalidate();
        this.eventManager.raiseEvent(new SessionEvent(this) {
            @Override
            public void dispatch(SessionEventHandler handler) {
                handler.onExpiration(this);
            }
        });
    }

    /**
     * @return the subject
     */
    public PicketBoxSubject getSubject() {
        return subject;
    }

    /**
     * <p>
     * Checks if the session is invalid.
     * </p>
     *
     * @throws PicketBoxSessionException in the case this instance is marked as invalid.
     */
    private void checkIfIsInvalid() throws PicketBoxSessionException {
        if (invalid)
            throw PicketBoxMessages.MESSAGES.invalidatedSession();
    }

    protected void setEventManager(PicketBoxEventManager eventManager) {
        this.eventManager = eventManager;
    }

}