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
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.handlers.UsernamePasswordAuthHandler;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class UserNamePasswordAuthenticationService extends AbstractAuthenticationService {

    public UserNamePasswordAuthenticationService(AuthenticationMechanism mechanism) {
        super(mechanism);
    }

    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> arrayList = new ArrayList<AuthenticationInfo>();

        arrayList.add(new AuthenticationInfo("Username and Password authentication service.",
                "A simple authentication service using a username and password as credentials.",
                UsernamePasswordAuthHandler.class));

        return arrayList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.picketbox.core.authentication.spi.AbstractAuthenticationService#doAuthenticate(org.picketbox.core.authentication.
     * AuthenticationManager, org.picketbox.core.authentication.api.AuthenticationCallbackHandler,
     * org.picketbox.core.authentication.api.AuthenticationResult)
     */
    @Override
    protected Principal doAuthenticate(AuthenticationManager authenticationManager,
            AuthenticationCallbackHandler callbackHandler, AuthenticationResult result) throws AuthenticationException {
        NameCallback nameCallback = new NameCallback("User name:");
        PasswordCallback passwordCallback = new PasswordCallback("Password:", false);

        try {
            callbackHandler.handle(new Callback[] { nameCallback, passwordCallback });
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }

        String userName = nameCallback.getName();
        String password = String.valueOf(passwordCallback.getPassword());

        return authenticationManager.authenticate(userName, password);
    }
}