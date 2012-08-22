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

package org.picketbox.core.authentication;

import java.util.List;

import org.picketbox.core.Credential;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>
 * This interface defines a specific authentication mechanism.
 * </p>
 * <p>
 * {@link AuthenticationMechanism} implementations hides the complexity and specific logic for a specific authentication
 * mechanism.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface AuthenticationMechanism {

    /**
     * <p>
     * Checks if the specified {@link Credential} class is supported by this mechanism.
     * </p>
     *
     * @param handlerClass
     * @return
     */
    boolean supports(Credential credential);

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
     * Performs authentication given the informations provided by the {@link Credential} instance.
     * </p>
     * <p>
     *
     * @param callbackHandler
     * @return
     * @throws AuthenticationException
     */
    AuthenticationResult authenticate(Credential credential) throws AuthenticationException;

}