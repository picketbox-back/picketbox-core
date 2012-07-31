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
import org.picketbox.core.PicketBoxAuthenticationConfig;
import org.picketbox.core.PicketBoxConfiguration;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.api.AuthenticationEvent;
import org.picketbox.core.authentication.api.AuthenticationEventHandler;
import org.picketbox.core.authentication.impl.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.authentication.spi.CertificateMechanism;
import org.picketbox.core.authentication.spi.DefaultAuthenticationEventManager;
import org.picketbox.core.authentication.spi.DigestMechanism;
import org.picketbox.core.authentication.spi.UserAuthenticatedEvent;
import org.picketbox.core.authentication.spi.UserAuthenticationEventHandler;
import org.picketbox.core.authentication.spi.UserNamePasswordMechanism;
import org.picketbox.core.authentication.spi.UsernamePasswordAuthHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class PicketBoxConfigurationTestCase {

    @Test
    public void testConfiguration() throws Exception {
        PicketBoxConfiguration configuration = new PicketBoxConfiguration();

        PicketBoxAuthenticationConfig authentication = configuration.authentication();
        
        authentication.addMechanism(new UserNamePasswordMechanism()).addMechanism(new DigestMechanism())
                .addMechanism(new CertificateMechanism());
        
        authentication.addAuthManager(new PropertiesFileBasedAuthenticationManager());
        authentication.addEventManager(new DefaultAuthenticationEventManager());
        authentication.addObserver(new UserAuthenticationEventHandler() {
            
            @Override
            public Class<? extends AuthenticationEvent<? extends AuthenticationEventHandler>> getEventType() {
                return UserAuthenticatedEvent.class;
            }
            
            @Override
            public void onSucessfullAuthentication(UserAuthenticatedEvent userAuthenticatedEvent) {
                System.out.println("Authenticated.");
            }
        });
        
        PicketBoxManager buildAndStart = configuration.buildAndStart();
        
        PicketBoxSubject authenticate = buildAndStart.authenticate(new UsernamePasswordAuthHandler("admin", "admin"));
        
        Assert.assertNotNull(authenticate);
    }

}
