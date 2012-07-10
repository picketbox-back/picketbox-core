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
package org.picketbox.authentication.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;

import org.picketbox.authentication.AuthenticationManager;

/**
 * Base class for all the HTTP authentication schemes
 *
 * @author anil saldhana
 * @since Jul 6, 2012
 */
public abstract class AbstractHTTPAuthentication implements HTTPAuthenticationScheme {
    /**
     * Injectable instance of Authentication Manager
     */
    protected AuthenticationManager authManager;

    /**
     * Injectable realm name
     */
    protected String realmName = HTTPAuthenticationScheme.REALM;

    /**
     * An instance of {@link ServletContext}
     */
    protected ServletContext servletContext = null;

    public AuthenticationManager getAuthManager() {
        return authManager;
    }

    public void setAuthManager(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}