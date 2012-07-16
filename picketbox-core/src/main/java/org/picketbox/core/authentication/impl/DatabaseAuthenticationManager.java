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
 * An instance of {@link org.picketbox.core.authentication.AuthenticationManager} that connects to a database to retrieve
 * stored passwords for authentication. It requires the configuration of a {@code DataSource} and a query that will be
 * used to obtain the password for the incoming username.
 * </p>
 * <p>
 * This manager offers the following configuratoin properties:
 * <ul>
 *     <li>dataSource: allows for the injection of a {@code DataSource} instance</li>
 *     <li>dsJNDIName: specifies the JNDI name that can be used to retrieve a {@code DataSource} instance. If the
 *     {@code DataSource} has not been injected directly, this property MUST be set. Otherwise, authentication will
 *     fail</li>
 *     <li>principalsQuery: required parameter that specifies the query that must be run in order to obtain the password
 *     associated with the incoming username. It must return a single result and must accept the username as a query
 *     parameter</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class DatabaseAuthenticationManager extends AbstractAuthenticationManager {

    private DataSource dataSource;

    private String dsJNDIName;

    private String principalsQuery;

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

    public String getPrincipalsQuery() {
        return this.principalsQuery;
    }

    public void setPrincipalsQuery(String query) {
        this.principalsQuery = query;
    }

    /**
     * <p>
     * Establishes a connection to the database to obtain the password associated with the specified username.
     * </p>
     *
     * @param username the username used as a parameter in the {@code principalsQuery}.
     * @return the password retrieved from the database.
     * @throws AuthenticationException if an error occurs while retrieving the {@code DataSource} or if query returns
     * no results.
     */
    private String retrievePasswordFromDatabase(String username) throws AuthenticationException {

        // TODO: add code to suspend/resume transactions via configuration.

        // if the datasource has not been injected, try obtaining it from JNDI.
        if (this.dataSource == null) {
            if (this.dsJNDIName != null) {
                try {
                    InitialContext context = new InitialContext();
                    this.dataSource = (DataSource) context.lookup(this.dsJNDIName);
                }
                catch (NamingException ne) {
                    throw new AuthenticationException(ne);
                }
            }
            else {
                throw PicketBoxMessages.MESSAGES.missingDataSourceConfiguration();
            }
        }

        // get a connection from the datasource and execute the principalsQuery.
        if (this.principalsQuery == null)
            throw PicketBoxMessages.MESSAGES.missingRequiredProperty("principalsQuery");

        PicketBoxLogger.LOGGER.debugQueryExecution(this.principalsQuery, username);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.dataSource.getConnection();
            preparedStatement = connection.prepareStatement(this.principalsQuery);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new AuthenticationException(PicketBoxMessages.MESSAGES.queryFoundNoResultsMessage(this.principalsQuery));

            return resultSet.getString(1);
        }
        catch (SQLException se) {
            throw new AuthenticationException(se);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException se) {
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException se) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException se) {
                }
            }
        }
    }
}
