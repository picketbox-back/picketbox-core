package org.picketbox.core.authentication.api;

import java.util.Map;

import org.picketbox.core.authentication.spi.AuthenticationProvider;

/**
 * <p>This interface represents a registry from which the @{link AuthenticationProvider} implementations are loaded.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
interface AuthenticationRegistry {

    /**
     * <p>Returns an {@link Map} where the key is the name for a specific {@link AuthenticationProvider} and the value is its the class name.</p>
     *
     * @return
     */
    Map<String, String> allProviders();

}