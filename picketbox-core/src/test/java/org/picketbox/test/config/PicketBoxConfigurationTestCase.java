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

package org.picketbox.test.config;

import junit.framework.Assert;

import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.authentication.event.AuthenticationEvent;
import org.picketbox.core.authentication.event.AuthenticationEventHandler;
import org.picketbox.core.authentication.impl.PicketBoxAuthenticationProvider;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.authorization.impl.SimpleAuthorizationManager;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.identity.DefaultIdentityManager;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class PicketBoxConfigurationTestCase {

    @Test
    public void testFluentConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder
            .authentication()
                .provider(new PicketBoxAuthenticationProvider())
                    .authManager(new PropertiesFileBasedAuthenticationManager())
                    .eventManager().handler(new AuthenticationEventHandler() {
                        
                        @Override
                        public Class<? extends AuthenticationEvent<? extends AuthenticationEventHandler>> getEventType() {
                            return null;
                        }
                    })
            .authorization()
                .manager(new SimpleAuthorizationManager())
            .identityManager()
                .manager(new DefaultIdentityManager());
        
        PicketBoxConfiguration build = builder.build();
        
        DefaultPicketBoxManager picketBoxManager = new DefaultPicketBoxManager(build);
        
        picketBoxManager.start();
        
        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();
        
        authenticatingSubject.setCredential(new UsernamePasswordCredential("admin", "admin"));
        
        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());

    }

}
