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

import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.session.PicketBoxSession;

/**
 * <p> {@link PicketBoxEventHandler} that handle {@link PicketBoxSession} related events.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface SessionEventHandler extends PicketBoxEventHandler {

    /**
     * <p>Handles the specified {@link SessionEvent} when a session is created.</p>
     *
     * @param sessionEvent
     */
    void onCreate(SessionEvent sessionEvent);

    /**
     * <p>Handles the specified {@link SessionEvent} when an attribute is updated.</p>
     *
     * @param sessionEvent
     * @param key
     * @param val
     */
    void onSetAttribute(SessionEvent sessionEvent, String key, Object val);

    /**
     * <p>Handles the specified {@link SessionEvent} when an attribute is requested.</p>
     *
     * @param sessionEvent
     * @param key
     */
    void onGetAttribute(SessionEvent sessionEvent, String key);

    /**
     * <p>Handles the specified {@link SessionEvent} when the session is invalidated.</p>
     *
     * @param sessionEvent
     */
    void onInvalidate(SessionEvent sessionEvent);

    /**
     * <p>Handles the specified {@link SessionEvent} when the session is expired.</p>
     *
     * @param sessionEvent
     */
    void onExpiration(SessionEvent sessionEvent);

}