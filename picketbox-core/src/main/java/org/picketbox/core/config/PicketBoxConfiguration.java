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

package org.picketbox.core.config;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxConfiguration {

    private AuthenticationConfiguration authentication;
    private AuthorizationConfig authorization;
    private IdentityManagerConfig identityManager;

    public PicketBoxConfiguration(AuthenticationConfiguration authentication, AuthorizationConfig authorization,
            IdentityManagerConfig identityManager) {
        this.authentication = authentication;
        this.authorization = authorization;
        this.identityManager = identityManager;
    }

    /**
     * @return the authentication
     */
    public AuthenticationConfiguration getAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the authentication to set
     */
    public void setAuthentication(AuthenticationConfiguration authentication) {
        this.authentication = authentication;
    }

    /**
     * @return the authorization
     */
    public AuthorizationConfig getAuthorization() {
        return authorization;
    }

    /**
     * @param authorization the authorization to set
     */
    public void setAuthorization(AuthorizationConfig authorization) {
        this.authorization = authorization;
    }

    /**
     * @return the identityManager
     */
    public IdentityManagerConfig getIdentityManager() {
        return identityManager;
    }

    /**
     * @param identityManager the identityManager to set
     */
    public void setIdentityManager(IdentityManagerConfig identityManager) {
        this.identityManager = identityManager;
    }

}
