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
package org.picketbox.core.ldap.handlers;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;

/**
 * Handler to obtain the initial context
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPContextHandler {

    protected BasicLDAPStoreConfig ldapStoreConfig;

    /**
     * Set the {@link BasicLDAPStoreConfig}
     *
     * @return
     */
    public BasicLDAPStoreConfig getLdapStoreConfig() {
        return ldapStoreConfig;
    }

    /**
     * Set the {@link BasicLDAPStoreConfig}
     *
     * @param ldapStoreConfig
     */
    public void setLdapStoreConfig(BasicLDAPStoreConfig ldapStoreConfig) {
        this.ldapStoreConfig = ldapStoreConfig;
    }

    /**
     * Execute the Handler
     *
     * @return
     */
    public DirContext execute() {
        Properties env = new Properties();

        String factoryName = ldapStoreConfig.getFactoryName();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);

        String authType = ldapStoreConfig.getSecurityAuthentication();
        env.setProperty(Context.SECURITY_AUTHENTICATION, authType);

        String protocol = ldapStoreConfig.getSecurityProtocol();
        if (protocol != null) {
            env.setProperty(Context.SECURITY_PROTOCOL, protocol);
        }

        String bindDN = ldapStoreConfig.getAdminName();
        char[] bindCredential = ldapStoreConfig.getAdminPassword();

        if (bindDN != null) {
            env.setProperty(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindCredential);
        }

        if (bindDN != null) {
            // Rebind the ctx to the bind dn/credentials for the roles searches
            env.setProperty(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindCredential);

        }

        env.setProperty(Context.PROVIDER_URL, ldapStoreConfig.getStoreURL());

        InitialLdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
        } catch (NamingException e1) {
            throw PicketBoxMessages.MESSAGES.ldapCtxConstructionFailure(e1);
        }
        return ctx;
    }
}