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

package org.picketbox.test.config;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserAuthenticationEventHandler;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.identity.IdentityManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * <p>
 * Tests the PicketBox configuration API.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxConfigurationTestCase {

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

    /**
     * <p>
     * Tests a simple configuration using only the default values.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testDefaultConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        PicketBoxConfiguration build = builder.build();

        DefaultPicketBoxManager picketBoxManager = new DefaultPicketBoxManager(build);

        picketBoxManager.start();

        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();

        authenticatingSubject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());
    }

    /**
     * <p>
     * Tests a simple configuration using only the default values.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testCustomIdentityManagerConfiguration() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.identityManager().manager(new IdentityManager() {

            @Override
            public PicketBoxSubject getIdentity(PicketBoxSubject resultingSubject) {
                List<String> roles = new ArrayList<String>();

                roles.add("test");

                resultingSubject.setRoleNames(roles);

                return resultingSubject;
            }
        });

        PicketBoxConfiguration build = builder.build();

        DefaultPicketBoxManager picketBoxManager = new DefaultPicketBoxManager(build);

        picketBoxManager.start();

        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();

        authenticatingSubject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());
        Assert.assertEquals("test", subject.getRoleNames().get(0));
    }

    /**
     * <p>
     * Tests a simple configuration using only the default values.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testDataBaseAuthenticationManager() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.authentication().dataBaseAuthManager().dataSource(dataSource)
                .passwordQuery("SELECT PASSWORD FROM USERS WHERE USERNAME = ?");

        PicketBoxConfiguration build = builder.build();

        DefaultPicketBoxManager picketBoxManager = new DefaultPicketBoxManager(build);

        picketBoxManager.start();

        PicketBoxSubject authenticatingSubject = new PicketBoxSubject();

        authenticatingSubject.setCredential(new UsernamePasswordCredential("picketbox", "goodpass"));

        PicketBoxSubject subject = picketBoxManager.authenticate(authenticatingSubject);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());
    }

}