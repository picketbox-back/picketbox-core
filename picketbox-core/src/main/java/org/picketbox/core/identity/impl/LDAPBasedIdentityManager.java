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

import org.jboss.picketlink.idm.internal.LDAPIdentityStore;
import org.jboss.picketlink.idm.internal.config.LDAPConfiguration;
import org.jboss.picketlink.idm.spi.IdentityStore;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.config.LDAPIdentityManagerConfiguration;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;

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
public class LDAPBasedIdentityManager extends AbstractDelegateIdentityManager {

    protected LDAPConfiguration ldapConfig = null;

    public LDAPBasedIdentityManager() {

    }

    public LDAPBasedIdentityManager(LDAPIdentityManagerConfiguration build) {
        this.ldapConfig = build.getStoreConfig();
    }

    /**
     * Set the {@link BasicLDAPStoreConfig}
     *
     * @param basicLdapConfig
     */
    public void setLDAPConfiguration(LDAPConfiguration basicLdapConfig) {
        this.ldapConfig = basicLdapConfig;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.identity.impl.AbstractDelegateIdentityManager#createIdentityStore()
     */
    @Override
    protected IdentityStore createIdentityStore() {
        if (ldapConfig == null) {
            throw PicketBoxMessages.MESSAGES.basicLdapConfigMissing();
        }

        LDAPIdentityStore store = new LDAPIdentityStore();

        store.setConfiguration(this.ldapConfig);

        return store;
    }


}