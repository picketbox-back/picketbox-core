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
package org.picketbox.authentication.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.picketbox.PicketBoxPrincipal;
import org.picketbox.authentication.AbstractAuthenticationManager;
import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.DigestHolder;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.util.HTTPDigestUtil;

/**
 * A simple username/password based {@link AuthenticationManager}
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public class SimpleCredentialAuthenticationManager extends AbstractAuthenticationManager {

    private Map<String, String> passMap = new HashMap<String, String>();

    public SimpleCredentialAuthenticationManager() {
        String username = SecurityActions.getSystemProperty("username", null);
        String pass = SecurityActions.getSystemProperty("pass", null);
        if (username != null && pass != null) {
            passMap.put(username, pass);
        }
    }

    public SimpleCredentialAuthenticationManager(Map<String, String> theMap) {
        this.passMap.putAll(theMap);
    }

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