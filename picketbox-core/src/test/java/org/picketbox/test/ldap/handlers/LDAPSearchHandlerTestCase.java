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
package org.picketbox.test.ldap.handlers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.DirContext;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.ldap.config.BasicLDAPStoreConfig;
import org.picketbox.core.ldap.config.LDAPSearchConfig;
import org.picketbox.core.ldap.handlers.LDAPContextHandler;
import org.picketbox.core.ldap.handlers.LDAPSearchHandler;
import org.picketbox.test.ldap.BaseOpenDS;

/**
 * Unit test the {@link LDAPSearchHandler}
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPSearchHandlerTestCase extends BaseOpenDS {

    protected String adminPW = "password";
    protected String dn = "dc=jboss,dc=org";
    protected String adminDN = "cn=Directory Manager";
    String port = "10389";

    @Before
    public void setup() throws Exception {
        URL ldif = getClass().getClassLoader().getResource("ldap/users.ldif");
        boolean op = util.addLDIF(serverHost, port, adminDN, adminPW, ldif);
        assertTrue(op);
    }

    @Test
    public void testSearch() throws Exception {

        BasicLDAPStoreConfig ldapStoreConfig = new BasicLDAPStoreConfig();
        ldapStoreConfig.setUserName(adminDN);
        ldapStoreConfig.setUserPassword(adminPW);
        ldapStoreConfig.setStoreURL("ldap://localhost:" + port);

        LDAPContextHandler ctx = new LDAPContextHandler();
        ctx.setLdapStoreConfig(ldapStoreConfig);

        DirContext dc = ctx.execute();
        assertNotNull(dc);

        LDAPSearchConfig searchConfig = new LDAPSearchConfig();
        searchConfig.setScope("subtree");
        searchConfig.setSearchBase("ou=Roles,dc=jboss,dc=org");
        searchConfig.setSearchAttributes(new String[] { "cn" });
        searchConfig.setSearchFilterExpression("member={0}");
        searchConfig.setFilterArgs(new Object[] { "uid=jduke,ou=People,dc=jboss,dc=org" });

        LDAPSearchHandler handler = new LDAPSearchHandler();
        handler.setLdapSearchConfig(searchConfig);
        List<String> values = handler.executeSearch(dc);
        assertNotNull(values);
        assertTrue(values.size() > 0);
        System.out.println(Arrays.toString(values.toArray()));
    }
}