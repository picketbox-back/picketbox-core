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
package org.picketbox.core.identity.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;
import org.picketbox.core.ldap.config.LDAPSearchConfig;
import org.picketbox.core.ldap.handlers.LDAPContextHandler;
import org.picketbox.core.ldap.handlers.LDAPSearchHandler;

/**
 * An instance of {@link IdentityManager} that obtains the information about an user from LDAP.
 *
 * <p/>
 * Based on org.jboss.security.auth.spi.LdapLoginModule
 * <p/>
 * Some of the prominent options
 * <p/>
 * java.naming.provider.url= ldap://localhost:10389/ principalDNPrefix uid= principalDNSuffix ",ou=People,dc=jboss,dc=org"
 *
 * @author Scott Stark
 * @author anil saldhana
 * @since Jul 17, 2012
 */
public class LDAPBasedIdentityManager implements IdentityManager {

    protected Map<String, String> options = new HashMap<String, String>();

    protected String bindDN, bindCredential;

    protected BasicLDAPStoreConfig basicLdapConfig = null;

    protected LDAPSearchConfig ldapSearchConfig = null;

    /**
     * Set the {@link BasicLDAPStoreConfig}
     *
     * @param basicLdapConfig
     */
    public void setBasicLdapConfig(BasicLDAPStoreConfig basicLdapConfig) {
        this.basicLdapConfig = basicLdapConfig;
    }

    /**
     * Set the {@link LDAPSearchConfig}
     *
     * @param ldapSearchConfig
     */
    public void setLdapSearchConfig(LDAPSearchConfig ldapSearchConfig) {
        this.ldapSearchConfig = ldapSearchConfig;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.identity.IdentityManager#getIdentity(org.picketbox.core.PicketBoxSubject)
     */
    @Override
    public PicketBoxSubject getIdentity(PicketBoxSubject subject) {
        if (subject == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("subject");
        }
        if (subject.getUser() == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("authenticated principal");
        }
        if (basicLdapConfig == null) {
            throw PicketBoxMessages.MESSAGES.basicLdapConfigMissing();
        }
        if (ldapSearchConfig == null) {
            throw PicketBoxMessages.MESSAGES.ldapSearchConfigMissing();
        }

        Principal principal = subject.getUser();

        ldapSearchConfig.substituteUser(principal.getName());

        LDAPContextHandler ldapContextHandler = new LDAPContextHandler();
        ldapContextHandler.setLdapStoreConfig(basicLdapConfig);
        DirContext dc = ldapContextHandler.execute();

        LDAPSearchHandler ldapSearchHandler = new LDAPSearchHandler();
        ldapSearchHandler.setLdapSearchConfig(ldapSearchConfig);

        List<String> roleNames = new ArrayList<String>();
        try {
            roleNames = ldapSearchHandler.executeSearch(dc);
        } catch (NamingException e) {
            throw PicketBoxMessages.MESSAGES.ldapSearchFailed(e);
        }

        subject.setRoleNames(roleNames);

        return subject;
    }

}