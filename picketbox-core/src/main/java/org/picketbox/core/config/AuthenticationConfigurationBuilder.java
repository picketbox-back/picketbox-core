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

import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class AuthenticationConfigurationBuilder extends AbstractConfigurationBuilder<AuthenticationConfiguration> {

    private EventManagerConfigurationBuilder eventManager;
    private AuthenticationProvider provider;
    private List<AuthenticationMechanism> mechanisms;
    private List<AuthenticationManager> authManagers;

    public AuthenticationConfigurationBuilder(ConfigurationBuilder builder) {
        super(builder);
        this.eventManager = new EventManagerConfigurationBuilder(this);
        this.mechanisms = new ArrayList<AuthenticationMechanism>();
        this.authManagers = new ArrayList<AuthenticationManager>();
    }

    public AuthenticationConfigurationBuilder provider(AuthenticationProvider authenticationProvider) {
        this.provider = authenticationProvider;
        return this;
    }

    public AuthenticationConfigurationBuilder provider() {
        return this;
    }

    @Override
    protected void setDefaults() {
        if (this.provider == null) {
            this.provider = new PicketBoxAuthenticationProvider();
        }
        if (this.authManagers.isEmpty()) {
            this.authManagers.add(new PropertiesFileBasedAuthenticationManager());
        }
    }

    public AuthenticationConfigurationBuilder mechanism(AuthenticationMechanism mechanism) {
        this.mechanisms.add(mechanism);
        return this;
    }

    public AuthenticationConfigurationBuilder authManager(AuthenticationManager authManager) {
        this.authManagers.add(authManager);
        return this;
    }

    public AuthenticationConfigurationBuilder authManager() {
        return this;
    }

    public AuthenticationConfigurationBuilder propertiesFileBased() {
        this.authManagers.add(new PropertiesFileBasedAuthenticationManager());
        return this;
    }

    public EventManagerConfigurationBuilder eventManager() {
        return this.eventManager;
    }

    @Override
    public AuthenticationConfiguration doBuild() {
        setDefaults();
        return new AuthenticationConfiguration(this.provider, this.authManagers, this.eventManager.build());
    }

}
