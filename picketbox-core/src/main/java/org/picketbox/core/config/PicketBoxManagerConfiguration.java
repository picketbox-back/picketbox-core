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

import org.picketbox.core.AbstractPicketBoxManager;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.exceptions.ConfigurationException;
import org.picketbox.core.identity.DefaultIdentityManager;
import org.picketbox.core.identity.IdentityManager;

/**
 * <p>
 * This class should be used to build the configuration and start the {@link PicketBoxHTTPManager}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
@SuppressWarnings({ "rawtypes" })
public final class PicketBoxManagerConfiguration {

    private AbstractPicketBoxManager picketBoxManager;

    private AuthorizationManager authorizationManager;
    private IdentityManager identityManager;

    private PicketBoxAuthenticationConfig authConfig;

    public PicketBoxManagerConfiguration() {
    }

    public PicketBoxAuthenticationConfig authentication() {
        if (this.authConfig == null) {
            this.authConfig = new PicketBoxAuthenticationConfig();
        }

        return this.authConfig;
    }

    /**
     * <pConfiguration method to register an @{link AuthorizationManager} instance.</p>
     *
     * @param authorizationManager
     * @return the configuration with the {@link AuthorizationManager} instance configured.
     */
    public PicketBoxManagerConfiguration authorization(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
        return this;
    }

    /**
     * <pConfiguration method to register an {@link IdentityManager} instance.</p>
     *
     * @param identityManager
     * @return the configuration with the {@link IdentityManager} instance configured.
     */
    public PicketBoxManagerConfiguration identityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
        return this;
    }

    /**
     * <p>
     * Create and starts a {@link PicketBoxHTTPManager} instance. Call this method when all configuration was done.
     * </p>
     *
     * @return a started {@link PicketBoxHTTPManager} instance.
     * @throws ConfigurationException if some error occur during the creation or startup of the {@link PicketBoxHTTPManager}
     *         instance. Or if the {@link PicketBoxHTTPManager} was already builded or started.
     */
    public PicketBoxManager buildAndStart() throws ConfigurationException {
        try {
            if (this.picketBoxManager == null) {
                this.picketBoxManager = new DefaultPicketBoxManager();
            }

            this.picketBoxManager.setAuthenticationProvider(this.authConfig.build());

            this.picketBoxManager.setAuthorizationManager(this.authorizationManager);

            if (this.identityManager == null) {
                this.identityManager = new DefaultIdentityManager();
            }

            this.picketBoxManager.setIdentityManager(this.identityManager);

            this.picketBoxManager.start();
        } catch (Exception e) {
            this.picketBoxManager = null;
            throw PicketBoxMessages.MESSAGES.failedToConfigurePicketBoxManager(e);
        }

        if (!picketBoxManager.started()) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerNotProperlyStarted();
        }

        return this.picketBoxManager;
    }

    public void manager(PicketBoxManager picketBoxManager) {
        this.picketBoxManager = (AbstractPicketBoxManager) picketBoxManager;
    }
}