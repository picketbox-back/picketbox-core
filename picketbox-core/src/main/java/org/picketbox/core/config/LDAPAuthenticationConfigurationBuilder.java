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

package org.picketbox.core.config;

import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class LDAPAuthenticationConfigurationBuilder extends AbstractConfigurationBuilder<BasicLDAPStoreConfig> {

    private BasicLDAPStoreConfig configuration = new BasicLDAPStoreConfig();

    public LDAPAuthenticationConfigurationBuilder(AuthenticationConfigurationBuilder authenticationConfigurationBuilder) {
        super(authenticationConfigurationBuilder);
    }

    public LDAPAuthenticationConfigurationBuilder storeURL(String storeUrl) {
        this.configuration.setStoreURL(storeUrl);
        return this;
    }

    public LDAPAuthenticationConfigurationBuilder userName(String userName) {
        this.configuration.setUserName(userName);
        return this;
    }

    public LDAPAuthenticationConfigurationBuilder factoryName(String factoryName) {
        this.configuration.setFactoryName(factoryName);
        return this;
    }

    public LDAPAuthenticationConfigurationBuilder userPassword(String password) {
        this.configuration.setUserPassword(password);
        return this;
    }

    public LDAPAuthenticationConfigurationBuilder protocol(String protocol) {
        this.configuration.setSecurityProtocol(protocol);
        return this;
    }

    public LDAPAuthenticationConfigurationBuilder userDN(String userDN) {
        this.configuration.setUserDN(userDN);
        return this;
    }

    @Override
    protected void setDefaults() {

    }

    @Override
    protected BasicLDAPStoreConfig doBuild() {
        return this.configuration;
    }

}
