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

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.impl.DatabaseAuthenticationManager;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>
 * Unit tests for the {@code DatabaseAuthenticationManager} class.
 * </p>
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class DatabaseAuthenticationManagerUnitTestCase {

    private static DataSource dataSource;

    @BeforeClass
    public static void setupDatabase() throws Exception {

        // disable the c3p0 log messages.
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");

        // setup the datasource that will be used in the tests.
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("org.h2.Driver");
        ds.setJdbcUrl("jdbc:h2:mem:test");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        // create the test table and add some test data.
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE USERS(username varchar2(20) not null, password varchar2(20) not null)");
        statement.execute("INSERT INTO USERS(username, password) VALUES ('picketbox', 'goodpass')");
        statement.close();
        connection.close();
    }

    @AfterClass
    public static void clearDatabase() throws Exception {

        // get a connection from the datasource and drop the test table.
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE USERS");
        statement.close();
        connection.close();
    }

    @Test
    public void testSuccessfulAuthViaJDBC() throws Exception {
        // create an instance of DBAuthManager and inject the datasource.
        DatabaseAuthenticationManager manager = new DatabaseAuthenticationManager();
        manager.setDataSource(dataSource);
        manager.setPasswordQuery("SELECT PASSWORD FROM USERS WHERE USERNAME = ?");

        // try to authenticate using a valid passoword.
        Principal principal = manager.authenticate("picketbox", "goodpass");
        Assert.assertNotNull(principal);
        Assert.assertTrue(principal instanceof PicketBoxPrincipal);
        Assert.assertEquals("picketbox", principal.getName());
    }

    @Test
    public void testUnsuccessfulAuthViaJDBC() throws Exception {
        DatabaseAuthenticationManager manager = new DatabaseAuthenticationManager();
        manager.setDataSource(dataSource);
        String query = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
        manager.setPasswordQuery(query);

        // try to authenticate using an invalid password.
        try {
            manager.authenticate("picketbox", "badpass");
            Assert.fail("Authentication should have failed - bad password has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertEquals(PicketBoxMessages.MESSAGES.failedToValidateCredentials().getMessage(), ae.getMessage());
        }

        // now try to authenticate using an invalid username.
        try {
            manager.authenticate("baduser", "badpass");
            Assert.fail("Authentication should have failed - invalid username has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertEquals(PicketBoxMessages.MESSAGES.queryFoundNoResultsMessage(query), ae.getMessage());
        }

        // lastly, try to authenticate using an invalid SQL query string.
        manager.setPasswordQuery("SELECT PASSWORD FROM ANOTHER_TABLE WHERE USERNAME = ?");
        try {
            manager.authenticate("picketbox", "goodpass");
            Assert.fail("Authentication should have failed - invalid query has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertTrue(ae.getCause() instanceof SQLException);
        }
    }

    @Test
    public void testSuccessfulAuthViaJPA() throws Exception {
        DatabaseAuthenticationManager manager = new DatabaseAuthenticationManager();
        manager.setPasswordQuery("SELECT PASSWORD FROM USERS WHERE USERNAME = ?");
        manager.setJpaConfigName("test");

        Principal principal = manager.authenticate("picketbox", "goodpass");
        Assert.assertNotNull(principal);
        Assert.assertTrue(principal instanceof PicketBoxPrincipal);
        Assert.assertEquals("picketbox", principal.getName());
    }

    @Test
    public void testUnsuccessfulAuthViaJPA() throws Exception {
        DatabaseAuthenticationManager manager = new DatabaseAuthenticationManager();
        String query = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
        manager.setPasswordQuery(query);
        manager.setJpaConfigName("test");

        // try to authenticate using an invalid password.
        try {
            manager.authenticate("picketbox", "badpass");
            Assert.fail("Authentication should have failed - bad password has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertEquals(PicketBoxMessages.MESSAGES.failedToValidateCredentials().getMessage(), ae.getMessage());
        }

        // now try to authenticate using an invalid username.
        try {
            manager.authenticate("baduser", "badpass");
            Assert.fail("Authentication should have failed - invalid username has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertTrue(ae.getCause() instanceof NoResultException);
        }

        // now try to use an invalid JPA configuration.
        manager.setJpaConfigName("badconfig");
        try {
            manager.authenticate("picketbox", "goodpass");
            Assert.fail("Authentication should have failed - invalid JPA configuration has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertTrue(ae.getCause() instanceof PersistenceException);
        }

        // finally try to authenticate using an invalid SQL query string.
        manager.setJpaConfigName("test");
        manager.setPasswordQuery("SELECT PASSWORD FROM ANOTHER_TABLE WHERE USERNAME = ?");
        try {
            manager.authenticate("picketbox", "goodpass");
            Assert.fail("Authentication should have failed - invalid query has been provided");
        } catch (AuthenticationException ae) {
            Assert.assertTrue(ae.getCause() instanceof PersistenceException);
        }

    }
}
