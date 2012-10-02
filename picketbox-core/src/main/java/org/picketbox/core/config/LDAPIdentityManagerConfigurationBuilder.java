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

import java.util.Properties;

import org.picketlink.idm.internal.config.LDAPConfiguration;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class LDAPIdentityManagerConfigurationBuilder extends AbstractConfigurationBuilder<LDAPIdentityManagerConfiguration> {

    private LDAPConfiguration ldapConfig = new LDAPConfiguration();

    public LDAPIdentityManagerConfigurationBuilder(IdentityManagerConfigurationBuilder identityManagerConfigurationBuilder) {
        super(identityManagerConfigurationBuilder);
    }

    public LDAPIdentityManagerConfigurationBuilder activeDirectory() {
        this.ldapConfig.setActiveDirectory(true);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder additionalProperties(Properties additionalProperties) {
        this.ldapConfig.setAdditionalProperties(additionalProperties);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder authType(String authType) {
        this.ldapConfig.setAuthType(authType);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder bindCredential(String bindCredential) {
        this.ldapConfig.setBindCredential(bindCredential);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder bindDN(String bindDN) {
        this.ldapConfig.setBindDN(bindDN);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder groupDNSuffix(String groupDNSuffix) {
        this.ldapConfig.setGroupDNSuffix(groupDNSuffix);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder url(String ldapURL) {
        this.ldapConfig.setLdapURL(ldapURL);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder protocol(String protocol) {
        this.ldapConfig.setProtocol(protocol);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder roleDNSuffix(String roleDNSuffix) {
        this.ldapConfig.setRoleDNSuffix(roleDNSuffix);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder standardAttributesFileName(String standardAttributesFileName) {
        this.ldapConfig.setStandardAttributesFileName(standardAttributesFileName);
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder userDNSuffix(String userDNSuffix) {
        this.ldapConfig.setUserDNSuffix(userDNSuffix);
        return this;
    }

    @Override
    protected void setDefaults() {
    }

    @Override
    protected LDAPIdentityManagerConfiguration doBuild() {
        return new LDAPIdentityManagerConfiguration(this.ldapConfig);
    }

}