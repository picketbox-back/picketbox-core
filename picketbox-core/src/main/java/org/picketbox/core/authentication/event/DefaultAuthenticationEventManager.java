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

package org.picketbox.core.authentication.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.picketbox.core.authentication.AuthenticationEventManager;

/**
 * Default Implementation of the {@link AuthenticationEventManager}
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class DefaultAuthenticationEventManager implements AuthenticationEventManager {

    @SuppressWarnings("rawtypes")
    private Map<Class<? extends AuthenticationEvent>, List<AuthenticationEventHandler>> observers = new HashMap<Class<? extends AuthenticationEvent>, List<AuthenticationEventHandler>>();

    public DefaultAuthenticationEventManager(List<AuthenticationEventHandler> handlers) {
        for (AuthenticationEventHandler handler : handlers) {
            addHandler(handler.getEventType(), handler);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.api.AuthenticationEventManager#raiseEvent(org.picketbox.core.authentication.api.
     * AuthenticationEvent)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void raiseEvent(AuthenticationEvent event) {
        List<AuthenticationEventHandler> handlers = this.observers.get(event.getClass());

        if (handlers != null) {
            for (AuthenticationEventHandler authenticationEventHandler : handlers) {
                event.dispatch(authenticationEventHandler);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void addHandler(Class<? extends AuthenticationEvent> eventType, AuthenticationEventHandler handler) {
        if (!this.observers.containsKey(eventType)) {
            this.observers.put(eventType, new ArrayList<AuthenticationEventHandler>());
        }

        List<AuthenticationEventHandler> handlers = this.observers.get(eventType);

        handlers.add(handler);
    }
}