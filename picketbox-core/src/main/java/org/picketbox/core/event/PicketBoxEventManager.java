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



/**
 * <p>
 * Authentication event managers are responsible for handle specific authentication events during the authentication.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface PicketBoxEventManager {

    /**
     * <p>
     * Raises an event.
     * </p>
     *
     * @param event
     */
    void raiseEvent(PicketBoxEvent<? extends PicketBoxEventHandler> event);

    /**
     * <p>Adds a {@link PicketBoxEventHandler}.</p>
     *
     * @param defaultSessionEventHandler
     */
    void addHandler(PicketBoxEventHandler handler);
}
