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

/**
 * <p>
 * Possible status values for the authentication process.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public enum AuthenticationStatus {

    /**
     * <p>
     * Successful authentication.
     * </p>
     */
    SUCCESS,

    /**
     * <p>
     * Authentication failed.
     * </p>
     */
    FAILED,

    /**
     * <p>
     * Provided credentials are invalid.
     * </p>
     */
    INVALID_CREDENTIALS,

    /**
     * <p>
     * The authentication process is not finished. More steps are needed.
     * </p>
     */
    CONTINUE,

    /**
     * <p>
     * This status means that no status was provided.
     * </p>
     */
    NONE

}
