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

package org.picketbox.core.authentication;

import java.util.List;

/**
 * <p>
 * This interface defines the contract for a Authentication Provider.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface AuthenticationProvider {

    /**
     * <p>
     * Returns the names for each supported mechanism.
     * </p>
     *
     * @return
     */
    String[] getSupportedMechanisms();

    /**
     * <p>
     * Checks if a specific mechanism is supported.
     * </p>
     *
     * @param mechanismName
     * @return
     */
    boolean supports(String mechanismName);

    /**
     * <p>
     * Returns a specific {@link AuthenticationMechanism} instance.
     * </p>
     *
     * @param string
     * @return
     */
    AuthenticationMechanism getMechanism(String string);

    /**
     * <p>
     * Returns the registered {@link AuthenticationManager} instances.
     * </p>
     *
     * @return
     */
    List<AuthenticationManager> getAuthenticationManagers();

    /**
     * <p>
     * Returns the registered {@link AuthenticationEventManager} instance.
     * </p>
     *
     * @return
     */
    AuthenticationEventManager getEventManager();

}
