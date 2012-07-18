/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.picketbox.test.ldap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.Test;

/**
 * Test Basic OpenDS functionality
 *
 * @author Anil Saldhana
 * @version $Revision$
 * @since Aug 23, 2007
 */
public class OpenDSUnitTestCase extends BaseOpenDS {

    @Test
    public void testLDAPAddDelete() throws Exception {
        URL ldif = getClass().getClassLoader().getResource("ldap/ldapAttributes.ldif");
        boolean op = util.addLDIF(serverHost, port, adminDN, adminPW, ldif);
        assertTrue(op);

        DirContext dc = null;
        NamingEnumeration<SearchResult> ne = null;
        try {
            dc = this.getDirContext();
            assertNotNull("DirContext exists?", dc);

            // Use JDK JNDI code for a search
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            ne = dc.search(dn, "(objectclass=*)", sc);
            while (ne.hasMore()) {
                SearchResult sr = ne.next();
                assertTrue("Search Result exists?", sr != null);
            }

            // We will delete the DIT just created
            assertTrue(util.deleteDNRecursively(serverHost, port, adminDN, adminPW, dn));

            assertFalse("The DIT does not exist", util.existsDN(serverHost, port, dn));
        } catch (Exception e) {
            System.err.println("Error in searching:");
            e.printStackTrace();
        } finally {
            if (ne != null)
                ne.close();
            if (dc != null)
                dc.close();
        }
    }

    protected void shutdown() throws Exception {
        // Check if the server is running
        if (opends.isRunning())
            opends.stopServer();
    }
}