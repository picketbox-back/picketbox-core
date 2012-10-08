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

package org.picketbox.core;

import org.picketbox.core.authorization.Resource;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.session.SessionManager;
import org.picketlink.idm.IdentityManager;

/**
 * <p>
 * A {@link PicketBoxManager} is responsible for providing all security capabilities for applications.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface PicketBoxManager extends PicketBoxLifecycle {

    /**
     * <p>
     * Authenticates an {@link UserContext}.
     * </p>
     *
     * @param subject
     * @return
     * @throws AuthenticationException
     */
    UserContext authenticate(UserContext subject) throws AuthenticationException;

    /**
     * <p>
     * Checks if the specified {@link UserContext} is authorized to access the specified {@link Resource}.
     * </p>
     *
     * @param subject
     * @param resource
     * @return
     */
    boolean authorize(UserContext subject, Resource resource);

    /**
     * <p>
     * Logout the specified {@link UserContext}.
     * </p>
     *
     * @param authenticatedUser
     * @throws IllegalStateException
     */
    void logout(UserContext authenticatedUser) throws IllegalStateException;

    /**
     * <p>Returns the configured {@link PicketBoxEventManager} instance.</p>
     *
     * @return
     */
    PicketBoxEventManager getEventManager();

    /**
     * <p>Returns the configured {@link IdentityManager} instance.</p>
     *
     * @return
     */
    IdentityManager getIdentityManager();

    /**
     * <p>Returns the configuration used to build and start an instance.</p>
     *
     * @return
     */
    PicketBoxConfiguration getConfiguration();

    /**
     * <p>Returns the configured {@link SessionManager} instance.</p>
     *
     * @return
     */
    SessionManager getSessionManager();

}