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

package org.picketbox.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default Implementation of the {@link PicketBoxEventManager}
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class DefaultEventManager implements PicketBoxEventManager {

    @SuppressWarnings("rawtypes")
    private Map<Class<? extends PicketBoxEvent>, List<PicketBoxEventHandler>> observers = new HashMap<Class<? extends PicketBoxEvent>, List<PicketBoxEventHandler>>();

    public DefaultEventManager(List<PicketBoxEventHandler> handlers) {
        for (PicketBoxEventHandler handler : handlers) {
            addHandler(handler);
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
    public void raiseEvent(PicketBoxEvent event) {
        List<PicketBoxEventHandler> handlers = this.observers.get(event.getClass());

        if (handlers == null) {
            handlers = this.observers.get(event.getClass().getSuperclass());
        }

        if (handlers != null) {
            for (PicketBoxEventHandler authenticationEventHandler : handlers) {
                event.dispatch(authenticationEventHandler);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void addHandler(PicketBoxEventHandler handler) {
        if (!this.observers.containsKey(handler.getEventType())) {
            this.observers.put(handler.getEventType(), new ArrayList<PicketBoxEventHandler>());
        }

        List<PicketBoxEventHandler> handlers = this.observers.get(handler.getEventType());

        handlers.add(handler);
    }

}