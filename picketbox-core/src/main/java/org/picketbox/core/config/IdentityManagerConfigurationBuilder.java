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

import org.picketbox.core.identity.PicketBoxSubjectPopulator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.internal.DefaultIdentityManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class IdentityManagerConfigurationBuilder extends AbstractConfigurationBuilder<GlobalIdentityManagerConfiguration> {

    private IdentityManager identityManager;

    @SuppressWarnings("unused")
    private AbstractConfigurationBuilder<? extends IdentityManagerConfiguration> identityManagerBuilder;

    private LDAPIdentityManagerConfigurationBuilder ldapIdentityManagerManager;
    private JPAIdentityManagerConfigurationBuilder jpaIdentityManagerManager;
    private FileIdentityManagerConfigurationBuilder fileIdentityManagerManager;

    private PicketBoxSubjectPopulator userPopulator;

    public IdentityManagerConfigurationBuilder(ConfigurationBuilder builder) {
        super(builder);
    }

    @Override
    protected void setDefaults() {
        if (this.identityManager == null) {
            this.identityManager = new DefaultIdentityManager();
        }

        if (identityManagerBuilder == null) {
            fileStore();
        }
    }

    public IdentityManagerConfigurationBuilder manager(IdentityManager identityManager) {
        this.identityManager = identityManager;
        return this;
    }

    public LDAPIdentityManagerConfigurationBuilder ldapStore() {
        if (this.ldapIdentityManagerManager == null) {
            this.ldapIdentityManagerManager = new LDAPIdentityManagerConfigurationBuilder(this);
        }
        this.identityManagerBuilder = this.ldapIdentityManagerManager;
        return this.ldapIdentityManagerManager;
    }

    public JPAIdentityManagerConfigurationBuilder jpaStore() {
        if (this.jpaIdentityManagerManager == null) {
            this.jpaIdentityManagerManager = new JPAIdentityManagerConfigurationBuilder(this);
        }
        this.identityManagerBuilder = this.jpaIdentityManagerManager;
        return this.jpaIdentityManagerManager;
    }

    public FileIdentityManagerConfigurationBuilder fileStore() {
        if (this.fileIdentityManagerManager == null) {
            this.fileIdentityManagerManager = new FileIdentityManagerConfigurationBuilder(this);
        }
        this.identityManagerBuilder = this.fileIdentityManagerManager;
        return this.fileIdentityManagerManager;
    }


    @Override
    public GlobalIdentityManagerConfiguration doBuild() {
        return new GlobalIdentityManagerConfiguration(this.identityManagerBuilder.build(), this.userPopulator);
    }

    public IdentityManagerConfigurationBuilder userPopulator(PicketBoxSubjectPopulator userPopulator) {
        this.userPopulator = userPopulator;
        return this;
    }

}
