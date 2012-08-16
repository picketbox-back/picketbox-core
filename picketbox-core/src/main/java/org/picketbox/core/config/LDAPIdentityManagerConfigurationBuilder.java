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
import org.picketbox.core.ldap.config.LDAPSearchConfig;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class LDAPIdentityManagerConfigurationBuilder extends AbstractConfigurationBuilder<LDAPIdentityManagerConfiguration> {

    private BasicLDAPStoreConfig storeConfig = new BasicLDAPStoreConfig();
    private LDAPSearchConfig searchConfig = new LDAPSearchConfig();

    public LDAPIdentityManagerConfigurationBuilder(IdentityManagerConfigurationBuilder identityManagerConfigurationBuilder) {
        super(identityManagerConfigurationBuilder);
    }

    public LDAPIdentityManagerConfigurationBuilder storeURL(String storeUrl) {
        this.storeConfig.setStoreURL(storeUrl);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder userName(String userName) {
        this.storeConfig.setUserName(userName);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder factoryName(String factoryName) {
        this.storeConfig.setFactoryName(factoryName);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder userPassword(String password) {
        this.storeConfig.setUserPassword(password);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder protocol(String protocol) {
        this.storeConfig.setSecurityProtocol(protocol);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder userDN(String userDN) {
        this.storeConfig.setUserDN(userDN);
        return this;
    }

    @Override
    protected void setDefaults() {
    }

    @Override
    protected LDAPIdentityManagerConfiguration doBuild() {
        return new LDAPIdentityManagerConfiguration(this.storeConfig, this.searchConfig);
    }

    public LDAPIdentityManagerConfigurationBuilder searchSubtree() {
        this.searchConfig.setScope("subtree");
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchOneLevel() {
        this.searchConfig.setScope("onelevel");
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchBase() {
        this.searchConfig.setScope("base");
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchBase(String searchBase) {
        this.searchConfig.setSearchBase(searchBase);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchAttributes(String[] attributes) {
        this.searchConfig.setSearchAttributes(attributes);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchFilterExpression(String expression) {
        this.searchConfig.setSearchFilterExpression(expression);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder searchFilterArgs(String[] filterArgs) {
        this.searchConfig.setFilterArgs(filterArgs);
        return this;
    }

}