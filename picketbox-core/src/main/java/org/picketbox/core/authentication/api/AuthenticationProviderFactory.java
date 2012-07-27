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

package org.picketbox.core.authentication.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.picketbox.core.authentication.spi.AuthenticationProvider;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class AuthenticationProviderFactory {

    private static AuthenticationProviderFactory instance;
    private static AuthenticationRegistry authenticationProviderRegistry;

    public static AuthenticationProviderFactory instance() {
        if (instance == null) {
            instance = new AuthenticationProviderFactory();
            authenticationProviderRegistry = ClassPathAuthenticationRegistry.instance();
        }

        return instance;
    }

    public static AuthenticationProviderFactory instance(AuthenticationRegistry registry) {
        if (instance == null) {
            instance = new AuthenticationProviderFactory();
        }

        return instance;
    }

    private final Map<String, AuthenticationProvider> cachedProviders = new HashMap<String, AuthenticationProvider>();

    private AuthenticationProviderFactory() {

    }

    public String[] getProviders() {
        Map<String, String> providersMap = authenticationProviderRegistry.allProviders();
        Set<Entry<String, String>> entrySet = providersMap.entrySet();

        String[] providers = new String[providersMap.size()];

        int i = 0;

        for (Entry<String, String> entry : entrySet) {
            providers[i++] = entry.getKey();
        }

        return providers;
    }

    public boolean supports(String authenticationName) {
        return ClassPathAuthenticationRegistry.instance().allProviders().containsKey(authenticationName);
    }

    public AuthenticationProvider getProvider(String authenticationName) {
        if (!supports(authenticationName)) {
            throw new SecurityException("No provider found for '" + authenticationName + "'. Possible providers are: " + getProviders());
        }

        AuthenticationProvider authenticationProvider = this.cachedProviders.get(authenticationName);

        if (authenticationProvider == null) {
            authenticationProvider = SecurityActions.newAuthenticationProviderInstance(ClassPathAuthenticationRegistry.instance().allProviders().get(authenticationName));

            authenticationProvider.initialize();

            this.cachedProviders.put(authenticationName, authenticationProvider);
        }

        return authenticationProvider;
    }

}