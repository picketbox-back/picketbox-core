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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.picketbox.core.authentication.spi.AuthenticationProvider;

/**
 * <p>A {@link AuthenticationRegistry} implementations that looks for the {@link AuthenticationProvider} configurations from the classpath.</p>
 * <p>In order to provide this configuration you need a <code>CONFIGURATION_FILE</code> with a content like:</p>
 * SASL=org.picketbox.core.test.authentication.spi.sasl.TestSASLAuthenticationProvider<br/>
 * HTTP=org.picketbox.core.test.authentication.spi.http.TestHTTPAuthenticationProvider<br/>
 * JAAS=org.picketbox.core.test.authentication.spi.TestJAASAuthenticationProvider<br/>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
final class ClassPathAuthenticationRegistry implements AuthenticationRegistry {

    private static final String CONFIGURATION_FILE = "META-INF/services/org.picketbox.core.authentication.provider";
    private static final AuthenticationRegistry instance;

    static {
        instance = new ClassPathAuthenticationRegistry();
    }

    private final Map<String, String> registry = new HashMap<String, String>();

    public static AuthenticationRegistry instance() {
        return instance;
    }

    private ClassPathAuthenticationRegistry() {
        loadRegistry();
    }

    private void loadRegistry() {
        ClassLoader classLoader = SecurityActions.getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(CONFIGURATION_FILE);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                Properties properties = new Properties();

                FileInputStream fis = new FileInputStream(url.getPath());

                properties.load(fis);

                Set<Entry<Object, Object>> entrySet = properties.entrySet();

                for (Entry<Object, Object> entry : entrySet) {
                    this.registry.put(entry.getKey().toString(), entry.getValue().toString());
                }

                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationRegistry#allProviders()
     */
    @Override
    public Map<String, String> allProviders() {
        return Collections.unmodifiableMap(this.registry);
    }

}
