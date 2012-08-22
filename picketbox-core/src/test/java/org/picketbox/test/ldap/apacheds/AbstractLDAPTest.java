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
package org.picketbox.test.ldap.apacheds;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for ldap tests
 *
 * @author anil saldhana
 * @since Jul 20, 2012
 */
public class AbstractLDAPTest {
    protected String adminPW = "secret";
    protected String dn = "dc=jboss,dc=org";
    protected String adminDN = "uid=admin,ou=system";
    protected String port = "10389";

    protected String serverHost = "localhost";

    protected EmbeddedApacheDS ds = null;

    @Before
    public void setup() throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");

        File workDir = new File(tempDir + "/server-work");
        workDir.mkdirs();

        ds = new EmbeddedApacheDS(workDir);
        ds.createBaseDN("apache", "dc=apache,dc=org");

        ds.createBaseDN("jboss", "dc=jboss,dc=org");

        long current = System.currentTimeMillis();
        System.out.println("Starting Apache DS server");
        ds.startServer();

        System.out.println("Time taken = " + (System.currentTimeMillis() - current) + "milisec");
    }

    @After
    public void tearDown() throws Exception {
        if (ds != null) {
            ds.stopServer();
        }
        String tempDir = System.getProperty("java.io.tmpdir");
        System.out.println("java.io.tmpdir=" + tempDir);

        System.out.println("Going to delete the server-work directory");
        File workDir = new File(tempDir + "/server-work");
        if (workDir != null) {
            recursiveDeleteDir(workDir);
        }
    }

    protected void importLDIF(String fileName) throws Exception {
        long current = System.currentTimeMillis();
        System.out.println("Going to import LDIF:" + fileName);
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        assertNotNull("LDIF file is not null?", is);
        ds.importLdif(is);
        System.out.println("Time taken = " + (System.currentTimeMillis() - current) + "milisec");
    }

    protected boolean recursiveDeleteDir(File dirPath) {
        if (dirPath.exists()) {
            File[] files = dirPath.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    recursiveDeleteDir(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        if (dirPath.exists())
            return dirPath.delete();
        else
            return true;
    }
}