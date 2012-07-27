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
package org.picketbox.core.authentication.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.picketbox.core.authentication.api.AuthenticationMechanism;
import org.picketbox.core.authentication.api.SecurityException;

/**
 * <p>Base class for {@link AuthenticationProvider} implementations.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    private final Map<String, AuthenticationMechanism> mechanisms = new HashMap<String, AuthenticationMechanism>();

    public AbstractAuthenticationProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#initialize()
     */
    public void initialize() {
        doAddMechanisms(this.mechanisms);
    }

    /**
     * <p>Subclasses should override this method to provide the supported {@link AuthenticationMechanism}.</p>
     *
     * @param mechanisms
     */
    protected abstract void doAddMechanisms(Map<String, AuthenticationMechanism> mechanisms);

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#getSupportedMechanisms()
     */
    public String[] getSupportedMechanisms() {
        Set<Entry<String, AuthenticationMechanism>> entrySet = this.mechanisms.entrySet();

        String[] mechanisms = new String[entrySet.size()];

        int i = 0;

        for (Entry<String, AuthenticationMechanism> entry : entrySet) {
            mechanisms[i++] = entry.getKey();
        }

        return mechanisms;
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#supports(java.lang.String)
     */
    public boolean supports(String mechanismName) {
        return this.mechanisms.containsKey(mechanismName);
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.spi.AuthenticationProvider#getMechanism(java.lang.String)
     */
    public AuthenticationMechanism getMechanism(String mechanismName) {
        if (!supports(mechanismName)) {
            throw new SecurityException("No mechanism found for '" + mechanismName + "'. Possible mechanisms are: " + getSupportedMechanisms());
        }

        return this.mechanisms.get(mechanismName);
    }

}