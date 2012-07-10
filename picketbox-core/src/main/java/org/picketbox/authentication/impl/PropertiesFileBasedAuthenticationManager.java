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

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Properties;

import org.picketbox.PicketBoxPrincipal;
import org.picketbox.authentication.AbstractAuthenticationManager;
import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.DigestHolder;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.util.HTTPDigestUtil;

/**
 * An instance of {@link AuthenticationManager} that uses a properties files users.properties for authentication
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public class PropertiesFileBasedAuthenticationManager extends AbstractAuthenticationManager {
    private Properties properties = new Properties();

    public PropertiesFileBasedAuthenticationManager() {
        InputStream is = SecurityActions.getClassLoader(getClass()).getResourceAsStream(PicketBoxConstants.USERS_PROPERTIES);
        if (is == null)
            throw new RuntimeException("properties file not found");
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            safeClose(is);
        }
    }

    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        String pass = properties.getProperty(username);
        if (pass != null && pass.equals(credential)) {
            return new PicketBoxPrincipal(username);
        }
        return null;
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {
        String username = digest.getUsername();
        String storedPass = properties.getProperty(username);
        if (storedPass != null) {
            if (HTTPDigestUtil.matchCredential(digest, storedPass.toCharArray())) {
                return new PicketBoxPrincipal(username);
            }
        }
        return null;
    }

    private void safeClose(InputStream is) {
        try {
            is.close();
        } catch (Exception e) {
        }
    }
}