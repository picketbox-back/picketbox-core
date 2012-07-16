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
package org.picketbox.core.authentication.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AbstractAuthenticationManager;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.HTTPDigestUtil;

/**
 * A simple username/password based {@link AuthenticationManager}
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public class SimpleCredentialAuthenticationManager extends AbstractAuthenticationManager {

    private Map<String, String> passMap = new HashMap<String, String>();

    /**
     * Default construction creates one entry (username,password) in the internal map using two system properties.
     * picketbox.username and picketbox.credential
     */
    public SimpleCredentialAuthenticationManager() {
        String username = SecurityActions.getSystemProperty(PicketBoxConstants.USERNAME, null);
        String pass = SecurityActions.getSystemProperty(PicketBoxConstants.CREDENTIAL, null);
        if (username != null && pass != null) {
            passMap.put(username, pass);
        }
    }

    /**
     * Pass in a map of username,password entries
     *
     * @param theMap
     */
    public SimpleCredentialAuthenticationManager(Map<String, String> theMap) {
        this.passMap.putAll(theMap);
    }

    /**
     * Set a {@link Map} of username/password
     *
     * @param pm
     */
    public void setPassMap(Map<String, String> pm) {
        this.passMap.clear();
        passMap.putAll(pm);
    }

    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        String pass = passMap.get(username);
        if (pass != null && pass.equals(credential)) {
            return new PicketBoxPrincipal(username);
        }
        return null;
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {
        String username = digest.getUsername();
        String storedPass = passMap.get(username);
        if (storedPass != null) {
            if (HTTPDigestUtil.matchCredential(digest, storedPass.toCharArray())) {
                return new PicketBoxPrincipal(username);
            }
        }
        return null;
    }
}