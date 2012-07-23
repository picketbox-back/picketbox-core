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

    protected String storeURL, userName, factoryName = "com.sun.jndi.ldap.LdapCtxFactory", securityAuthentication = "simple";
    protected String userPassword;

    protected String securityProtocol;

    protected String userDN = null;

    public BasicLDAPStoreConfig() {
    }

    /**
     * Set the LDAP URL
     *
     * @param storeURL
     */
    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }

    /**
     * Set the User DN Name
     *
     * @param adminName
     */
    public void setUserName(String adminName) {
        this.userName = adminName;
    }

    /**
     * Set the User Password
     *
     * @param adminPassword
     */
    public void setUserPassword(String adminPassword) {
        this.userPassword = adminPassword;
    }

    @Override
    public String getStoreURL() {
        return storeURL;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getUserPassword() {
        return userPassword;
    }

    public String getFactoryName() {
        return factoryName;
    }

    /**
     * In use cases, where the admin DN is different from user DN, then the {@link #substituteUser(String)} method is
     * ineffective
     *
     * @param userDN
     */
    public void setUserDN(String userDN) {
        this.userDN = userDN;
    }

    /**
     * Set the factory name of the JNDI Implementation
     *
     * @param factoryName
     */
    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    /**
     * Set optionally security authentication Default is set to "simple"
     *
     * @param securityAuthentication
     */
    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    /**
     * Set optionally security protocol such as "ssl"
     *
     * @param securityAuthentication
     */
    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    /**
     * Call this method when there is a need to substitute the current username with the real user name
     *
     * @param un
     */
    public void substituteUser(String un) {
        if (this.userName.contains("CHANGE_USER")) {
            this.userName = this.userName.replace("CHANGE_USER", un);
        } else if (userDN != null) {
            this.userName = userDN.replace("CHANGE_USER", un);
        } else {
            // Look for the first '=' sign
            int index = this.userName.indexOf('=');
            if (index > 0) {
                String uid = this.userName.substring(0, index);

                int commaIndex = this.userName.indexOf(',', index);
                if (commaIndex > 0) {
                    String afterComma = this.userName.substring(commaIndex + 1);
                    this.userName = uid + "=" + un + "," + afterComma;
                }
            }
        }
    }
}