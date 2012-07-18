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
package org.picketbox.core.ldap.config;

import org.picketbox.core.config.BasicIdentityStoreConfig;

/**
 * Stores the basic ldap configuration
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class BasicLDAPStoreConfig implements BasicIdentityStoreConfig {

    protected String storeURL, adminName, factoryName = "com.sun.jndi.ldap.LdapCtxFactory", securityAuthentication = "simple";
    protected char[] adminPassword;

    protected String securityProtocol;

    public BasicLDAPStoreConfig() {
    }

    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setAdminPassword(char[] adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public String getStoreURL() {
        return storeURL;
    }

    @Override
    public String getAdminName() {
        return adminName;
    }

    @Override
    public char[] getAdminPassword() {
        return adminPassword;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }
}