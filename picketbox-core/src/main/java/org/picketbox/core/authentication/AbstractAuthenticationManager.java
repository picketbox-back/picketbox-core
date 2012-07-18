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
import java.util.HashMap;
import java.util.Map;

import org.picketbox.core.exceptions.AuthenticationException;

/**
 * An abstract instance of {@link AuthenticationManager} This class exists primarily for subclasses to just override the methods
 * they intend to implement.
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public abstract class AbstractAuthenticationManager implements AuthenticationManager {
    protected boolean started = false, stopped = false;

    protected Map<String, Object> options = new HashMap<String, Object>();

    /**
     * Set the options
     *
     * @param theOpt
     */
    public void setOptions(Map<String, Object> theOpt) {
        this.options.clear();
        this.options.putAll(theOpt);
    }

    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        throw new RuntimeException();
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {
        throw new RuntimeException();
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) throws AuthenticationException {
        throw new RuntimeException();
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public boolean stopped() {
        return stopped;
    }

    @Override
    public void stop() {
        stopped = true;
    }
}
