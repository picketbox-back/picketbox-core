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
package org.picketbox.test.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.authentication.impl.LDAPAuthenticationManager;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;
import org.picketbox.test.ldap.apacheds.AbstractLDAPTest;

/**
 * Unit test the {@link LDAPAuthenticationManager}
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class LDAPAuthenticationManagerUnitTestCase extends AbstractLDAPTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        importLDIF("ldap/ldapAttributes.ldif");
    }

    @Test
    public void testAuth() throws Exception {
        LDAPAuthenticationManager auth = new LDAPAuthenticationManager();

        Map<String, Object> options = new HashMap<String, Object>();

        options.put("userDN", "uid=CHANGE_USER,ou=People,dc=jboss,dc=org");

        auth.setOptions(options);

        BasicLDAPStoreConfig config = new BasicLDAPStoreConfig();
        config.setUserName("uid=CHANGE_USER,ou=People,dc=jboss,dc=org");
        config.setUserPassword("WILL_BE_REPLACED");
        config.setStoreURL("ldap://localhost:10389/");

        auth.setLdapStoreConfig(config);
        Principal principal = auth.authenticate("jduke", "theduke");

        assertNotNull(principal);
        assertEquals("jduke", principal.getName());
    }
}