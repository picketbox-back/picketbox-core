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

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.exceptions.AuthenticationException;

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
     * Authenticates an user using the specified {@link AuthenticationCallbackHandler}. If the the user is valid a
     * {@link PicketBoxSubject} is returned back.
     * </p>
     *
     * @param authenticationCallbackHandler
     * @return
     * @throws AuthenticationException
     */
    PicketBoxSubject authenticate(AuthenticationCallbackHandler authenticationCallbackHandler) throws AuthenticationException;

    /**
     * <p>
     * Authenticates an user using the specified {@link AuthenticationCallbackHandler}. If the the user is valid a
     * {@link PicketBoxSubject} is returned back. This method should be used when the authentication relies on informations
     * provided by the underlying environment like web-based applications. You would prefer using this method when there is a
     * specific {@link PicketBoxManager} implementation which customizes how the security capabilities are provided for a
     * specific environment.
     * </p>
     *
     * @param securityContext
     * @param authHandler
     * @return
     * @throws AuthenticationException
     */
    PicketBoxSubject authenticate(PicketBoxSecurityContext securityContext, AuthenticationCallbackHandler authHandler)
            throws AuthenticationException;

    /**
     * <p>
     * Checks if the specified {@link PicketBoxSubject} is authorized to access the specified {@link Resource}.
     * </p>
     *
     * @param subject
     * @param resource
     * @return
     */
    boolean authorize(PicketBoxSubject subject, Resource resource);

    /**
     * <p>
     * Creates a {@link PicketBoxSubject} given the specified {@link PicketBoxSecurityContext}. Each {@link PicketBoxManager}
     * implementation have its own way to create subjects, the {@link PicketBoxSecurityContext} encapsulates the informations
     * required to build an empty subject or retrieve an authenticated one.
     * </p>
     *
     * @param securityContext
     * @return
     */
    PicketBoxSubject createSubject(PicketBoxSecurityContext securityContext);

    /**
     * <p>Logout the specified {@link PicketBoxSubject}.</p>
     *
     * @param authenticatedUser
     * @throws IllegalStateException
     */
    void logout(PicketBoxSubject authenticatedUser) throws IllegalStateException;
}