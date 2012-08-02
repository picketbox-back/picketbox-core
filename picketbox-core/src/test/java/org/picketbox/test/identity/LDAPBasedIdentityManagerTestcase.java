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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.impl.LDAPBasedIdentityManager;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;
import org.picketbox.core.ldap.config.LDAPSearchConfig;
import org.picketbox.test.ldap.apacheds.AbstractLDAPTest;

/**
 * Unit test the {@link LDAPBasedIdentityManager}
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPBasedIdentityManagerTestcase extends AbstractLDAPTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        importLDIF("ldap/users.ldif");
    }

    @Test
    public void testIdentity() throws Exception {
        BasicLDAPStoreConfig basicLdapStoreConfig = new BasicLDAPStoreConfig();
        basicLdapStoreConfig.setStoreURL("ldap://localhost:10389/");
        basicLdapStoreConfig.setUserName("uid=jduke,ou=People,dc=jboss,dc=org");
        basicLdapStoreConfig.setUserPassword("theduke");

        LDAPSearchConfig searchConfig = new LDAPSearchConfig();

        searchConfig.setScope("subtree");
        searchConfig.setSearchBase("ou=Roles,dc=jboss,dc=org");
        searchConfig.setSearchAttributes(new String[] { "cn" });
        searchConfig.setSearchFilterExpression("member={0}");
        searchConfig.setFilterArgs(new String[] { "uid=jduke,ou=People,dc=jboss,dc=org" });

        LDAPBasedIdentityManager im = new LDAPBasedIdentityManager();
        im.setBasicLdapConfig(basicLdapStoreConfig);
        im.setLdapSearchConfig(searchConfig);

        PicketBoxSubject subject = im.getIdentity(new PicketBoxPrincipal("jduke"));
        assertNotNull(subject);
        List<String> roleNames = subject.getRoleNames();
        assertTrue(roleNames != null && roleNames.size() > 0);
        assertTrue(roleNames.contains("Echo"));
        assertTrue(roleNames.contains("TheDuke"));
    }
}