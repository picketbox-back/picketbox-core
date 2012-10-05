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

package org.picketbox.test;

import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Group;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

/**
 * <p>
 * Base class for test cases that allows to create a fresh {@link PicketBoxManager} instance using some specific
 * {@link PicketBoxConfiguration}. This class also initializes the identity store with the default user information.
 * </p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public abstract class AbstractDefaultPicketBoxManagerTestCase {

    private PicketBoxManager picketboxManager;

    protected PicketBoxManager getPicketBoxManager(PicketBoxConfiguration configuration) {
        if (this.picketboxManager == null) {
            this.picketboxManager = new DefaultPicketBoxManager(configuration);
            this.picketboxManager.start();
            initialize(this.picketboxManager.getIdentityManager());
        }

        return this.picketboxManager;
    }

    /**
     * <p>Initializes the identity manager store with users information.</p>
     * 
     * @param identityManager
     */
    private void initialize(IdentityManager identityManager) {
        User adminUser = identityManager.createUser("admin");

        adminUser.setEmail("admin@picketbox.com");
        adminUser.setFirstName("The");
        adminUser.setLastName("Admin");

        identityManager.updatePassword(adminUser, "admin");

        Role roleDeveloper = identityManager.createRole("developer");
        Role roleAdmin = identityManager.createRole("admin");

        Group groupCoreDeveloper = identityManager.createGroup("PicketBox Group");

        identityManager.grantRole(roleDeveloper, adminUser, groupCoreDeveloper);
        identityManager.grantRole(roleAdmin, adminUser, groupCoreDeveloper);
    }

}
