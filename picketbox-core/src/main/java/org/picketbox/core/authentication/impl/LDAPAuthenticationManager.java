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

import javax.naming.directory.DirContext;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AbstractAuthenticationManager;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;
import org.picketbox.core.ldap.handlers.LDAPContextHandler;

/**
 * An instance of {@link AuthenticationManager} that uses LDAP for authentication.
 *
 * For configuration, there is a need to inject the {@link BasicLDAPStoreConfig}
 *
 * Additionally, an option of userDN needs to be configured.
 *
 * Example: "uid=CHANGE_USER,ou=People,dc=jboss,dc=org" This Manager will substitute the keyword CHANGE_USER with the username,
 * it is trying to authenticate.
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class LDAPAuthenticationManager extends AbstractAuthenticationManager {

    private BasicLDAPStoreConfig ldapStoreConfig = null;

    /**
     * Set an instance of {@link BasicLDAPStoreConfig}
     *
     * @param ldapStoreConfig
     */
    public void setLdapStoreConfig(BasicLDAPStoreConfig ldapStoreConfig) {
        this.ldapStoreConfig = ldapStoreConfig;
    }

    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        boolean isValid = false;
        try {
            // Validate the password by trying to create an initial context
            createLdapInitContext(username, credential);
            isValid = true;
        } catch (Throwable e) {
            throw new AuthenticationException(e);
        }
        if (isValid) {
            return new PicketBoxPrincipal(username);
        }
        return null;
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {
        throw new AuthenticationException("Not Implemented");
    }

    private void createLdapInitContext(String username, Object credential) throws AuthenticationException {

        String userDNString = (String) options.get("userDN");
        if (userDNString == null)
            throw PicketBoxMessages.MESSAGES.userDNStringMissing();

        if (ldapStoreConfig == null)
            throw PicketBoxMessages.MESSAGES.ldapStoreConfigMissing();

        if (ldapStoreConfig != null) {
            LDAPContextHandler handler = new LDAPContextHandler();
            String user = userDNString.replace("CHANGE_USER", username);
            ldapStoreConfig.setUserName(user);
            ldapStoreConfig.setUserPassword(credential.toString().toCharArray());
            handler.setLdapStoreConfig(ldapStoreConfig);
            DirContext dir = handler.execute();
            if (dir != null) {
                return;
            } else {
                throw PicketBoxMessages.MESSAGES.authenticationFailed(null);
            }
        }
    }
}