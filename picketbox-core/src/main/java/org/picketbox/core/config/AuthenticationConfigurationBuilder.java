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

import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.impl.CertificateAuthenticationMechanism;
import org.picketbox.core.authentication.impl.DigestAuthenticationMechanism;
import org.picketbox.core.authentication.impl.UserNamePasswordAuthenticationMechanism;

/**
 * A {@link ConfigurationBuilder} for Authentication
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class AuthenticationConfigurationBuilder extends AbstractConfigurationBuilder<AuthenticationConfiguration> {

    protected List<AuthenticationMechanism> mechanisms;

    public AuthenticationConfigurationBuilder(ConfigurationBuilder builder) {
        super(builder);
        this.mechanisms = new ArrayList<AuthenticationMechanism>();
    }

    public AuthenticationConfigurationBuilder mechanism(AuthenticationMechanism mechanism) {
        this.mechanisms.add(mechanism);
        return this;
    }

    @Override
    protected void setDefaults() {
        if (this.mechanisms.isEmpty()) {
            this.mechanisms.add(new UserNamePasswordAuthenticationMechanism());
            this.mechanisms.add(new DigestAuthenticationMechanism());
            this.mechanisms.add(new CertificateAuthenticationMechanism());
        }
    }

    @Override
    public AuthenticationConfiguration doBuild() {
        return new AuthenticationConfiguration(this.mechanisms, this.builder.eventManager().build());
    }
}