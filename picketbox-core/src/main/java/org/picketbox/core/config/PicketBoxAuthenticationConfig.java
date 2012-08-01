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

import org.picketbox.core.authentication.AuthenticationEventHandler;
import org.picketbox.core.authentication.AuthenticationEventManager;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.event.DefaultAuthenticationEventManager;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxAuthenticationConfig {

    private AuthenticationProvider provider;
    private List<AuthenticationMechanism> mechanisms = new ArrayList<AuthenticationMechanism>();
    private List<AuthenticationManager> authManagers = new ArrayList<AuthenticationManager>();
    private AuthenticationEventManager eventManager = new DefaultAuthenticationEventManager();

    public PicketBoxAuthenticationConfig provider(AuthenticationProvider provider) {
        this.provider = provider;
        return this;
    }

    public PicketBoxAuthenticationConfig addMechanism(AuthenticationMechanism mechanism) {
        this.mechanisms.add(mechanism);
        return this;
    }

    public PicketBoxAuthenticationConfig addAuthManager(AuthenticationManager authManager) {
        this.authManagers.add(authManager);
        return this;
    }

    public AuthenticationProvider build() {
        if (provider == null) {
            provider = new PicketBoxAuthenticationProvider();
        }

        this.provider.initialize();

        for (AuthenticationMechanism mechanism : this.mechanisms) {
            mechanism.setAuthenticationProvider(this.provider);
            provider.addMechanism(mechanism);
        }

        for (AuthenticationManager manager : this.authManagers) {
            provider.addAuthManager(manager);
        }

        this.provider.setEventManager(this.eventManager);

        return provider;
    }

    public void addEventManager(AuthenticationEventManager authenticationEventManager) {
        this.eventManager = authenticationEventManager;
    }

    /**
     * @param handler
     */
    public void addObserver(AuthenticationEventHandler handler) {
        this.eventManager.addHandler(handler.getEventType(), handler);
    }
}
