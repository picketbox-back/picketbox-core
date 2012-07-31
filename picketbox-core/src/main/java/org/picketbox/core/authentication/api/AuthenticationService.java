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

package org.picketbox.core.authentication.api;

import java.util.List;

import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>
 * This interface defines a server view of a specific {@link AuthenticationMechanism}.
 * </p>
 * <p>
 * {@link AuthenticationService} implementations provide an abstraction for servers hiding from them the complexity and specific
 * logic for a specific {@link AuthenticationMechanism}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface AuthenticationService {

    /**
     * <p>
     * Checks if the specified {@link AuthenticationCallbackHandler} class is supported by this service.
     * </p>
     *
     * @param handlerClass
     * @return
     */
    boolean supportsHandler(Class<? extends AuthenticationCallbackHandler> handlerClass);

    /**
     * <p>
     * Returns a list of {@link AuthenticationInfo} with informations about what is needed before to proceeding with the
     * authentication.
     * </p>
     *
     * @return
     */
    List<AuthenticationInfo> getAuthenticationInfo();

    /**
     * <p>
     * Performs authentication given the informations provided by the {@link AuthenticationCallbackHandler} instance.
     * </p>
     * <p>
     * To perform the authentication you should also provide a {@link AuthenticationUser} instance that will be used to populate
     * with all security info collected during the authentication process.
     * </p>
     *
     * @param callbackHandler
     * @return
     * @throws AuthenticationException
     */
    AuthenticationResult authenticate(AuthenticationCallbackHandler callbackHandler)
            throws AuthenticationException;

    AuthenticationResult authenticate(String realm, AuthenticationCallbackHandler callbackHandler)
            throws AuthenticationException;

}