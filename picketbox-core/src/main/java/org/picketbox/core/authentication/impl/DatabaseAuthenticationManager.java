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
package org.picketbox.core.authentication.impl;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.picketbox.core.PicketBoxLogger;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AbstractAuthenticationManager;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.util.HTTPDigestUtil;

/**
 * <p>
 * An instance of {@link org.picketbox.core.authentication.AuthenticationManager} that connects to a database to retrieve stored
 * passwords for authentication. Both JPA and JDBC can be used to connect to the database and perform the queries.
 * </p>
 * <p>
 * When using JPA, the following properties MUST be configured;
 * <ul>
 * <li>jpaConfigName; the name of the persistence unit as configured in the persistence.xml file</li>
 * <li>passwordQuery; the query that must be run in order to obtain the password associated with the incoming username. It must
 * return a single result and must accept the username as a query parameter.</li>
 * </ul>
 * </p>
 * <p>
 * When using JDBC, the manager requires the configuration of a {@code DataSource}. The following properties are available for
 * JDBC mode:
 * <ul>
 * <li>dataSource; allows for direct injection of a {@code DataSource} instance</li>
 * <li>dsJndiName; specifies the JNDI name that can be used to retrieve a {@code DataSource} instance. If the {@code DataSource}
 * has not been injected directly, this property MUST be set. Otherwise, authentication will fail</li>
 * <li>passwordQuery; the query that must be run in order to obtain the password associated with the incoming username. It must
 * return a single result and must accept the username as a query parameter.</li>
 * </ul>
 * </p>
 * <p>
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class DatabaseAuthenticationManager extends AbstractAuthenticationManager {

    private DataSource dataSource;

    private String dsJNDIName;

    private String jpaConfigName;

    private String passwordQuery;

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getDsJNDIName() {
        return this.dsJNDIName;
    }

    public void setDsJNDIName(String dsJNDIName) {
        this.dsJNDIName = dsJNDIName;
    }

    public String getJpaConfigName() {
        return this.jpaConfigName;
    }

    public void setJpaConfigName(String configuration) {
        this.jpaConfigName = configuration;
    }

    public String getPasswordQuery() {
        return this.passwordQuery;
    }

    public void setPasswordQuery(String query) {
        this.passwordQuery = query;
    }

    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {

        String password = this.retrievePasswordFromDatabase(username);
        if (password != null && password.equals(credential))
            return new PicketBoxPrincipal(username);
        else
            throw PicketBoxMessages.MESSAGES.failedToValidateCredentials();
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {

        String username = digest.getUsername();
        String password = this.retrievePasswordFromDatabase(username);
        if (password != null && HTTPDigestUtil.matchCredential(digest, password.toCharArray()))
            return new PicketBoxPrincipal(username);
        else
            throw PicketBoxMessages.MESSAGES.failedToValidateCredentials();
    }

    /**
     * <p>
     * Establishes a connection to the database to obtain the password associated with the specified username. If a JPA
     * configuration name has been provided, JPA will be used to retrieve the password. If not, the code will attempt to use a
     * DataSource to establish a JDBC connection to the database.
     * </p>
     *
     * @param username the username used as a parameter in the {@code passwordQuery}.
     * @return the password retrieved from the database. <<<<<<< HEAD
     * @throws AuthenticationException if an error occurs while retrieving password from the database. =======
     * @throws AuthenticationException if an error occurs while retrieving the {@code DataSource} or if query returns no
     *         results. >>>>>>> 7758681... ldap based stuff
     */
    private String retrievePasswordFromDatabase(String username) throws AuthenticationException {

        // check if the required principals query property has been set..
        if (this.getPasswordQuery() == null || this.getPasswordQuery().isEmpty())
            throw PicketBoxMessages.MESSAGES.missingRequiredProperty("passwordQuery");

        // if the name of a JPA configuration has been set, use it to execute the query via JPA.
        if (this.getJpaConfigName() != null) {
            return this.retrievePasswordViaJPA(username);
        }

        // no JPA config has been supplied - try getting a reference to a datasource.
        if (this.getDataSource() == null) {
            if (this.getDsJNDIName() != null) {
                try {
                    InitialContext context = new InitialContext();
                    this.dataSource = (DataSource) context.lookup(this.getDsJNDIName());
                } catch (NamingException ne) {
                    throw new AuthenticationException(ne);
                }
            } else {
                throw PicketBoxMessages.MESSAGES.invalidDatabaseAuthenticationManagerConfiguration();
            }
        }

        // use the datasource to execute the query via JDBC.
        return this.retrievePasswordViaJDBC(username);
    }

    /**
     * <p>
     * This method uses the configured {@code DataSource} to get a connection to the database and execute the password query. It
     * expects the query to return a single result and to accept the incoming username as a parameter.
     * </p>
     *
     * @param username the username used as a parameter in the {@code passwordQuery}.
     * @return the password retrieved from the dabase.
     * @throws AuthenticationException if an error occurs while retrieving the password via JDBC.
     */
    private String retrievePasswordViaJDBC(String username) throws AuthenticationException {

        // TODO: add code to suspend/resume transactions via configuration.
        PicketBoxLogger.LOGGER.debugQueryExecution(this.getPasswordQuery(), username);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();
            preparedStatement = connection.prepareStatement(this.getPasswordQuery());
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new AuthenticationException(
                        PicketBoxMessages.MESSAGES.queryFoundNoResultsMessage(this.getPasswordQuery()));

            return resultSet.getString(1);
        } catch (SQLException se) {
            throw new AuthenticationException(se);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * <p>
     * This method uses a JPA configuration to connect to the database and execute the password query. It is expected that a
     * persistence.xml file has been properly configured with a persistence unit whose name matches the name provided in the
     * {@code jpaConfigName} property.
     * </p>
     * <p>
     * The query follows the same rule as the JDBC query: it must return a single result containing the password and must accept
     * the incoming username as a query parameter.
     * </p>
     *
     * @param username the username used as a parameter in the {@code passwordQuery}.
     * @return the password retrieved from the dabase.
     * @throws AuthenticationException if an error occurs while retrieving the password via JPA.
     */
    private String retrievePasswordViaJPA(String username) throws AuthenticationException {

        try {
            // get an entity manager factory using the jpa configuration name.
            EntityManagerFactory factory = Persistence.createEntityManagerFactory(this.getJpaConfigName());
            EntityManager manager = factory.createEntityManager();

            // create a query instance and run the configured principals query.
            Query query = manager.createNativeQuery(this.getPasswordQuery());
            query.setParameter(1, username);

            Object result = query.getSingleResult();
            return result.toString();
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }

    }
}
