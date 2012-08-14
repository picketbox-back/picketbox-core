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


/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractConfigurationBuilder<T> {

    protected AbstractConfigurationBuilder<?> builder;

    public AbstractConfigurationBuilder() {
    }

    protected AbstractConfigurationBuilder(AbstractConfigurationBuilder<?> builder) {
        this.builder = builder;
    }

    /**
     * @return
     * @see org.picketbox.core.config.ConfigurationBuilder#authentication()
     */
    public AuthenticationConfigurationBuilder authentication() {
        return builder.authentication();
    }

    /**
     * @return
     * @see org.picketbox.core.config.ConfigurationBuilder#authorization()
     */
    public AuthorizationConfigurationBuilder authorization() {
        return builder.authorization();
    }

    /**
     * @return
     * @see org.picketbox.core.config.ConfigurationBuilder#identityManager()
     */
    public IdentityManagerConfigurationBuilder identityManager() {
        return builder.identityManager();
    }

    public EventManagerConfigurationBuilder eventManager() {
        return this.builder.eventManager();
    }

    public SessionManagerConfigurationBuilder sessionManager() {
        return this.builder.sessionManager();
    }


    /**
     * <p>Subclasses should override to provide default values for missing configurations.</p>
     */
    protected abstract void setDefaults();

    /**
     * <p>Creates a T instance with all defined configurations.</p>
     *
     * @return
     */
    public T build() {
        setDefaults();
        return doBuild();
    }

    /**
     * <p>Subclasses should override this method to create a specific T instance.</p>
     *
     * @return
     */
    protected abstract T doBuild();
}
