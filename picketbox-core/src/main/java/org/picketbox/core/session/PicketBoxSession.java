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

    protected transient List<PicketBoxSessionListener> listeners = new ArrayList<PicketBoxSessionListener>();

    /**
     * Usable by {@link PicketBoxSessionManager#create()}
     */
    public PicketBoxSession(SessionId<? extends Serializable> id) {
        this.id = id;
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
        if (invalid)
            throw PicketBoxMessages.MESSAGES.invalidatedSession();
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
        if (invalid)
            throw PicketBoxMessages.MESSAGES.invalidatedSession();
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
        if (invalid)
            throw PicketBoxMessages.MESSAGES.invalidatedSession();
        return attributes.get(key);
    }

    /**
     * Is the session valid?
     *
     * @return
     */
    public boolean isValid() {
        return invalid == false;
    }

    /**
     * Invalidate the session
     */
    public void invalidate() {
        for (PicketBoxSessionListener listener : listeners) {
            listener.onInvalidate(this);
        }
        attributes.clear();
        invalid = true;
    }

    /**
     * Expire the session
     */
    public void expire() {
        invalidate();
        for (PicketBoxSessionListener listener : listeners) {
            listener.onExpiration(this);
        }
    }
}