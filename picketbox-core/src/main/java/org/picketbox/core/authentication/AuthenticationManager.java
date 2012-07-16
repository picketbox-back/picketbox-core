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

import java.security.Principal;
import java.security.cert.X509Certificate;

import org.picketbox.core.PicketBoxLifecycle;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * Interface used for authentication
 *
 * @author anil saldhana
 * @since July 5, 2012
 */
public interface AuthenticationManager extends PicketBoxLifecycle{
    /**
     * Authenticate an user based on a Credential
     *
     * @param username
     * @param credential
     * @return
     * @throws AuthenticationException
     */
    Principal authenticate(String username, Object credential) throws AuthenticationException;

    /**
     * Authenticate an user using the HTTP/Digest Mechanism
     *
     * @param digest
     * @return
     * @throws AuthenticationException
     */
    Principal authenticate(DigestHolder digest) throws AuthenticationException;

    /**
     * Authenticate using {@link X509Certificate}
     *
     * @param certs
     * @return
     * @throws AuthenticationException
     */
    Principal authenticate(X509Certificate[] certs) throws AuthenticationException;
}