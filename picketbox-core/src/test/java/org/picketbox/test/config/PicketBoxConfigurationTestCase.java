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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.identity.PicketBoxSubjectPopulator;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;

/**
 * <p>
 * Tests the PicketBox configuration API.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxConfigurationTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>
     * Tests a simple configuration using only the default values.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testDefaultConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();

        authenticatingSubject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
    }

    /**
     * <p>
     * Tests a simple configuration using only the default values.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testCustomSubjectPopulatorConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.identityManager().userPopulator(new PicketBoxSubjectPopulator() {

            @Override
            public PicketBoxSubject getIdentity(PicketBoxSubject authenticatedSubject) {
                List<Role> roles = new ArrayList<Role>();

                roles.add(new SimpleRole("test"));

                authenticatedSubject.setRoles(roles);

                return authenticatedSubject;
            }
        });

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();

        authenticatingSubject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        assertNotNull(subject);
        assertTrue(subject.isAuthenticated());
        assertTrue(subject.hasRole("test"));
    }

}