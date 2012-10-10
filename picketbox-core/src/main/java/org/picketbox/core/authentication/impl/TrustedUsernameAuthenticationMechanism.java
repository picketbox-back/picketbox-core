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

import org.picketbox.core.Credential;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.credential.TrustedUsernameCredential;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketlink.idm.model.User;

/**
 * <p>
 * A {@link AuthenticationMechanism} implementation for a X.509 Certificate based authentication.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class TrustedUsernameAuthenticationMechanism extends AbstractAuthenticationMechanism {

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.api.AuthenticationService#getAuthenticationInfo()
     */
    @Override
    public List<AuthenticationInfo> getAuthenticationInfo() {
        List<AuthenticationInfo> arrayList = new ArrayList<AuthenticationInfo>();

        arrayList.add(new AuthenticationInfo("Trusts the provided username and check if it maps to a valid user account.",
                "Trust the provided username and check if it maps to a valid user account.", TrustedUsernameCredential.class));

        return arrayList;

    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.impl.AbstractAuthenticationMechanism#doAuthenticate(org.picketbox.core.Credential,
     * org.picketbox.core.authentication.AuthenticationResult)
     */
    @Override
    protected Principal doAuthenticate(Credential credential, AuthenticationResult result) throws AuthenticationException {
        User user = getIdentityManager().getUser(credential.getUserName());

        if (user != null) {
            return new PicketBoxPrincipal(user.getKey());
        }

        return null;
    }
}
