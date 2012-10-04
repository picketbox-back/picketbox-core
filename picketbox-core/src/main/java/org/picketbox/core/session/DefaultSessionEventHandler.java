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

import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.exceptions.PicketBoxSessionException;
import org.picketbox.core.session.event.SessionEvent;
import org.picketbox.core.session.event.SessionEventHandler;

/**
 * <p>Built-in implementation for {@link SessionEventHandler} that delegate the processing for the {@link SessionManager}.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DefaultSessionEventHandler implements SessionEventHandler {

    private SessionManager sessionManager;

    public DefaultSessionEventHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
        return SessionEvent.class;
    }

    @Override
    public void onCreate(SessionEvent sessionEvent) {

    }

    @Override
    public void onSetAttribute(SessionEvent sessionEvent, String key, Object val) {
        this.sessionManager.update(sessionEvent.getSession());
    }

    @Override
    public void onGetAttribute(SessionEvent sessionEvent, String key) {
        PicketBoxSession storedSession = this.sessionManager.retrieve(sessionEvent.getSession().getId());

        if (storedSession != null) {
            try {
                sessionEvent.getSession().setAttribute(key, storedSession.getAttributes().get(key));
            } catch (PicketBoxSessionException e) {
                throw new IllegalStateException("Unable to update session with stored attribute.", e);
            }
        }
    }

    @Override
    public void onInvalidate(SessionEvent sessionEvent) {
        this.sessionManager.remove(sessionEvent.getSession());
    }

    @Override
    public void onExpiration(SessionEvent sessionEvent) {
        this.sessionManager.remove(sessionEvent.getSession());
    }

}
