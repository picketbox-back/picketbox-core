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

package org.picketbox.core;

import org.picketbox.core.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.exceptions.ConfigurationException;

/**
 * <p>
 * This class should be used to build the configuration and start the {@link PicketBoxManager}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class PicketBoxConfiguration {

    private PicketBoxManager picketBoxManager;

    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private IdentityManager identityManager;

    public PicketBoxConfiguration() {
    }

    /**
     * <pConfiguration method to register a @{link HTTPAuthenticationScheme} instance.</p>
     *
     * @param authenticationScheme
     * @return the configuration with the {@link HTTPAuthenticationScheme} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration authentication(HTTPAuthenticationScheme authenticationScheme) throws ConfigurationException {
        this.authenticationScheme = authenticationScheme;
        return this;
    }

    /**
     * <pConfiguration method to register an @{link AuthorizationManager} instance.</p>
     *
     * @param authorizationManager
     * @return the configuration with the {@link AuthorizationManager} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration authorization(AuthorizationManager authorizationManager) throws ConfigurationException {
        this.authorizationManager = authorizationManager;
        return this;
    }

    /**
     * <pConfiguration method to register an {@link IdentityManager} instance.</p>
     *
     * @param identityManager
     * @return the configuration with the {@link AuthorizationManager} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration identityManager(IdentityManager identityManager) throws ConfigurationException {
        this.identityManager = identityManager;
        return this;
    }

    /**
     * <p>
     * Create and starts a {@link PicketBoxManager} instance. Call this method when all configuration was done.
     * </p>
     *
     * @return a started {@link PicketBoxManager} instance.
     * @throws ConfigurationException if some error occur during the creation or startup of the {@link PicketBoxManager}
     *         instance. Or if the {@link PicketBoxManager} was already builded or started.
     */
    public PicketBoxManager buildAndStart() throws ConfigurationException {
        if (picketBoxManager != null) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerAlreadyStarted();
        }

        try {
            picketBoxManager = new PicketBoxManager(this.authenticationScheme);

            picketBoxManager.setAuthorizationManager(this.authorizationManager);

            if (this.identityManager == null) {
                this.identityManager = new DefaultIdentityManager();
            }

            picketBoxManager.setIdentityManager(identityManager);
            
            picketBoxManager.start();
        } catch (Exception e) {
            picketBoxManager = null;
            throw PicketBoxMessages.MESSAGES.failedToConfigurePicketBoxManager(e);
        }

        if (!picketBoxManager.started()) {
            throw PicketBoxMessages.MESSAGES.picketBoxManagerNotProperlyStarted();
        }

        return picketBoxManager;
    }

}