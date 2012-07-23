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
package org.picketbox.test.ldap.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;

/**
 * Unit test the {@link BasicLDAPStoreConfig}
 *
 * @author anil saldhana
 * @since Jul 23, 2012
 */
public class BasicLdapConfigUnitTestCase {

    @Test
    public void testSubstitution() throws Exception {
        BasicLDAPStoreConfig config = new BasicLDAPStoreConfig();
        config.setUserName("uid=admin,xyz");

        config.substituteUser("anil");

        String user = config.getUserName();
        assertEquals("uid=anil,xyz", user);

        config.setUserDN("uid=CHANGE_USER,ou=People");
        config.setUserName("CN=Directory Manager");

        config.substituteUser("anil");

        user = config.getUserName();
        assertEquals("uid=anil,ou=People", user);
    }
}