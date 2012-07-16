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

import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authorization.AuthorizationManager;
import org.picketbox.exceptions.ConfigurationException;

/**
 * <p>
 * This class should be used to build the configuration and start the {@link PicketBoxManager}.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public final class PicketBoxConfiguration {

    private static PicketBoxConfiguration instance;
    private static PicketBoxManager picketBoxManager;

    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private IdentityManager identityManager;

    private PicketBoxConfiguration() {
        // Singleton
    }

    /**
     * <p>
     * This method initializes the PicketBox configurations for use.
     * </p>
     * 
     * @return
     */
    public static PicketBoxConfiguration configure() {
        if (instance == null) {
            instance = new PicketBoxConfiguration();
        }

        return instance;
    }

    /**
     * <pConfiguration method to register a @{link HTTPAuthenticationScheme} instance. Before calling this method make sure the
     * configuration was properly initialized using the <code>configure</configure> method.</p>
     * 
     * @param authenticationScheme
     * @return the configuration with the {@link HTTPAuthenticationScheme} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration authentication(HTTPAuthenticationScheme authenticationScheme) throws ConfigurationException {
        checkConfigurationInitialized();
        this.authenticationScheme = authenticationScheme;
        return this;
    }

    /**
     * <pConfiguration method to register a @{link AuthorizationManager} instance. Before calling this method make sure the
     * configuration was properly initialized using the <code>configure</configure> method.</p></p>
     * 
     * @param authorizationManager
     * @return the configuration with the {@link AuthorizationManager} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration authorization(AuthorizationManager authorizationManager) throws ConfigurationException {
        checkConfigurationInitialized();
        this.authorizationManager = authorizationManager;
        return this;
    }

    /**
     * <pConfiguration method to register a @{link {@link IdentityManager} instance. Before calling this method make sure the
     * configuration was properly initialized using the <code>configure</configure> method.</p></p>
     * 
     * @param identityManager
     * @return the configuration with the {@link AuthorizationManager} instance configured.
     * @throws if the configuration was not previously initialized.
     */
    public PicketBoxConfiguration identityManager(IdentityManager identityManager) throws ConfigurationException {
        checkConfigurationInitialized();
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
        checkConfigurationInitialized();
        
        if (picketBoxManager != null) {
            throw new ConfigurationException("PicketBoxManager can be built and started only once.");
        }

        if (this.authenticationScheme == null) {
            throw new ConfigurationException("No authentication scheme provided.");
        }

        try {
            picketBoxManager = new PicketBoxManager();

            picketBoxManager.setAuthenticationScheme(this.authenticationScheme);
            picketBoxManager.setAuthorizationManager(this.authorizationManager);
            
            if (this.identityManager == null) {
                this.identityManager = new DefaultIdentityManager();
            }
            
            picketBoxManager.setIdentityManager(identityManager);
            
            picketBoxManager.start();
        } catch (Exception e) {
            picketBoxManager = null;
            throw new ConfigurationException("Could not build and start PicketBoxManager.", e);
        }

        if (!picketBoxManager.started()) {
            throw new ConfigurationException("PicketBoxManager was not properly started.");
        }

        return picketBoxManager;
    }

    /**
     * <p>
     * Checks if the configuration was previously initialized. Usually calling the <code>configure</code> method.
     * </p>
     */
    private void checkConfigurationInitialized() throws ConfigurationException {
        if (instance == null) {
            throw new ConfigurationException(
                    "Configuration not initialized. Did you forget to call the configure() method first ?");
        }
    }

}