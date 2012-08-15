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

import org.picketbox.core.session.InMemorySessionStore;
import org.picketbox.core.session.PicketBoxSessionListener;
import org.picketbox.core.session.SessionManager;
import org.picketbox.core.session.SessionStore;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SessionManagerConfigurationBuilder extends AbstractConfigurationBuilder<SessionManagerConfig> {

    private SessionManager manager;
    private SessionStore store;
    private List<PicketBoxSessionListener> listeners = new ArrayList<PicketBoxSessionListener>();
    private int sessionTimeout;

    public SessionManagerConfigurationBuilder(ConfigurationBuilder configurationBuilder) {
        super(configurationBuilder);
    }

    public SessionManagerConfigurationBuilder inMemorySessionStore() {
        if (this.store == null) {
            this.store = new InMemorySessionStore();
        }

        return this;
    }

    public SessionManagerConfigurationBuilder store(SessionStore store) {
        this.store = store;
        return this;
    }

    public SessionManagerConfigurationBuilder listener(PicketBoxSessionListener picketBoxSessionListener) {
        this.listeners.add(picketBoxSessionListener);
        return this;
    }

    public SessionManagerConfigurationBuilder sessionTimeout(int timeoutInMinutes) {
        this.sessionTimeout = timeoutInMinutes;
        return this;
    }

    @Override
    protected void setDefaults() {
    }

    @Override
    protected SessionManagerConfig doBuild() {
        return new SessionManagerConfig(this.manager, this.store, this.listeners, this.sessionTimeout);
    }

}
