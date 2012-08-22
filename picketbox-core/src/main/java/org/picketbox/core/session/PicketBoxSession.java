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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.exceptions.PicketBoxSessionException;

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

    protected transient List<PicketBoxSessionListener> listeners = new ArrayList<PicketBoxSessionListener>();

    public PicketBoxSession() {
        this(new DefaultSessionId());
    }

    public PicketBoxSession(PicketBoxSubject subject, SessionId<? extends Serializable> id) {
        this(id);
        this.subject = subject;
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
     * Add an attribute
     *
     * @param key
     * @param val
     * @throws PicketBoxSessionException
     */
    public void setAttribute(String key, Object val) throws PicketBoxSessionException {
        checkIfIsInvalid();
        attributes.put(key, val);
        for (PicketBoxSessionListener listener : listeners) {
            listener.onSetAttribute(this, key, val);
        }
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
    public Object getAttribute(String key) throws PicketBoxSessionException {
        checkIfIsInvalid();
        for (PicketBoxSessionListener listener : listeners) {
            listener.onGetAttribute(this);
        }
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
            for (PicketBoxSessionListener listener : listeners) {
                listener.onInvalidate(this);
            }
        }
        this.attributes.clear();
        if (this.subject != null) {
            this.subject.invalidate();
        }
        invalid = true;
    }

    /**
     * Expire the session
     *
     * @throws PicketBoxSessionException
     */
    public void expire() throws PicketBoxSessionException {
        invalidate();
        for (PicketBoxSessionListener listener : listeners) {
            listener.onExpiration(this);
        }
    }

    /**
     * @return the subject
     */
    public PicketBoxSubject getSubject() {
        return subject;
    }

    public boolean hasListener(Class<PicketBoxSessionStoreListener> class1) {
        for (PicketBoxSessionListener listener : listeners) {
            if (listener.getClass().equals(class1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a session listener
     *
     * @param listener
     */
    protected void addListener(PicketBoxSessionListener listener) {
        listeners.add(listener);
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

}