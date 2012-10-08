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

package org.picketbox.core.session.event;

import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;

/**
 * <p>
 * Simple class adapter for the {@link SessionEventHandler} interface to make easier the creation of session event handlers.
 * Instead of overriding all methods from the {@link SessionEventHandler} interface you can override only the ones that you want
 * to.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class SessionEventHandlerAdapter implements SessionEventHandler {

    @Override
    public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
        return SessionEvent.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.event.SessionEventHandler#onCreate(org.picketbox.core.session.event.SessionEvent)
     */
    @Override
    public void onCreate(SessionEvent sessionEvent) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.event.SessionEventHandler#onSetAttribute(org.picketbox.core.session.event.SessionEvent,
     * java.lang.String, java.lang.Object)
     */
    @Override
    public void onSetAttribute(SessionEvent sessionEvent, String key, Object val) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.event.SessionEventHandler#onGetAttribute(org.picketbox.core.session.event.SessionEvent,
     * java.lang.String)
     */
    @Override
    public void onGetAttribute(SessionEvent sessionEvent, String key) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.event.SessionEventHandler#onInvalidate(org.picketbox.core.session.event.SessionEvent)
     */
    @Override
    public void onInvalidate(SessionEvent sessionEvent) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.session.event.SessionEventHandler#onExpiration(org.picketbox.core.session.event.SessionEvent)
     */
    @Override
    public void onExpiration(SessionEvent sessionEvent) {

    }

}
