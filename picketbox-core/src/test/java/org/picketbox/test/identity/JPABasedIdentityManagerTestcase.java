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
package org.picketbox.test.identity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.identity.impl.EntityManagerContext;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;
import org.picketlink.test.idm.internal.jpa.AbstractJPAIdentityManagerTestCase;

/**
 * Unit test the {@link JPABasedIdentityManager}
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 */
public class JPABasedIdentityManagerTestcase extends AbstractJPAIdentityManagerTestCase {

    @Test
    public void testIdentity() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.identityManager().jpaStore();

        PicketBoxManager picketBoxManager = new DefaultPicketBoxManager(builder.build());

        picketBoxManager.start();

        EntityManagerContext.set(this.entityManager);

        IdentityManager identityManager = picketBoxManager.getIdentityManager();
        
        User adminUser = identityManager.createUser("admin");

        adminUser.setEmail("admin@picketbox.com");
        adminUser.setFirstName("The");
        adminUser.setLastName("Admin");

        identityManager.updatePassword(adminUser, "123");

        Role roleDeveloper = identityManager.createRole("developer");
        Role roleAdmin = identityManager.createRole("admin");

        Group groupCoreDeveloper = identityManager.createGroup("PicketBox Group");

        identityManager.grantRole(roleDeveloper, adminUser, groupCoreDeveloper);
        identityManager.grantRole(roleAdmin, adminUser, groupCoreDeveloper);

        UserContext subject = new UserContext();

        subject.setCredential(new UsernamePasswordCredential("admin", "123"));

        subject = picketBoxManager.authenticate(subject);

        assertNotNull(subject);

        // user was loaded by the identity manager ?
        assertNotNull(subject.getUser());

        assertTrue(subject.hasRole("admin"));
        assertTrue(subject.hasRole("developer"));

        EntityManagerContext.clear();
    }
}