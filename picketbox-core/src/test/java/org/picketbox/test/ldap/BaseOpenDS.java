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
package org.picketbox.test.ldap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.junit.After;
import org.junit.Before;

/**
 * Base Class for OpenDS tests
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 *
 */
public class BaseOpenDS {

    protected String serverHost;
    protected String port = "10389";
    protected String adminPW = "password";
    protected String dn = "dc=jboss,dc=org";
    protected String adminDN = "cn=Directory Manager";
    protected OpenDSUtil util = new OpenDSUtil();

    /**
     * Use a different value for the system property on a JVM that is not shipped by Sun
     */
    protected String ldapCtxFactory = System.getProperty("ldapctx.factory", "com.sun.jndi.ldap.LdapCtxFactory");

    protected String baseDir = System.getProperty("user.dir");
    protected String fs = System.getProperty("file.separator");

    // System property when running in eclipse (-Declipse=jbosssx/ )
    private String eclipsePath = System.getProperty("eclipse", "");

    protected String targetDir = eclipsePath + "target" + fs + "test-classes" + fs;
    protected String openDSDir = targetDir + "opends";

    protected OpenDS opends = null;

    @Before
    public void setUp() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Going to set up OpenDS");
        // Ensure openDSDir exists and recycle opends db dir
        File openDSDirFile = new File(openDSDir);
        if (openDSDirFile.exists()) {
            System.out.println("Recycle OpenDS DB dir");
            File dbDir = new File(openDSDir + fs + "db");
            assertTrue("Deletion of opendsDir db success", recursiveDeleteDir(dbDir));
            assertTrue("Creation of opendsDir DB success", dbDir.mkdirs());
            System.out.println("Done:Recycle OpenDS DB dir");
        }

        serverHost = "localhost";

        opends = new OpenDS();
        opends.intialize(openDSDir);
        if (opends.isRunning())
            opends.stopServer();
        opends.startServer();
        assertTrue(opends.isRunning());

        System.out.println("Done set up OpenDS:" + (System.currentTimeMillis() - start));
    }

    @After
    public void tearDown() throws Exception {
        assertTrue("DS is running", opends.isRunning());
        shutdown();
        assertFalse("DS is not running", opends.isRunning());
    }

    protected void shutdown() throws Exception {
        // Check if the server is running
        if (opends.isRunning())
            opends.stopServer();
    }

    protected DirContext getDirContext() throws Exception {
        String url = "ldap://" + serverHost + ":" + port;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapCtxFactory);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, adminDN);
        env.put(Context.SECURITY_CREDENTIALS, adminPW);
        return new InitialDirContext(env);
    }

    private boolean recursiveDeleteDir(File dirPath) {
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