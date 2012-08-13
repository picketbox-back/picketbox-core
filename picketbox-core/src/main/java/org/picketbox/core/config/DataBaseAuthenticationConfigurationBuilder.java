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

package org.picketbox.core.config;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DataBaseAuthenticationConfigurationBuilder extends AbstractConfigurationBuilder<DataBaseAuthenticationConfiguration> {

    private DataBaseAuthenticationConfiguration configuration = new DataBaseAuthenticationConfiguration();

    public DataBaseAuthenticationConfigurationBuilder dataSource(DataSource dataSource) {
        this.configuration.setDataSource(dataSource);
        return this;
    }

    public DataBaseAuthenticationConfigurationBuilder dataSourceJndiName(String name) {
        this.configuration.setDsJNDIName(name);
        return this;
    }

    public DataBaseAuthenticationConfigurationBuilder jpaPersistenceUnitName(String name) {
        this.configuration.setJpaConfigName(name);
        return this;
    }

    public DataBaseAuthenticationConfigurationBuilder jpaJndiName(String name) {
        this.configuration.setJpaJNDIName(name);
        return this;
    }

    public DataBaseAuthenticationConfigurationBuilder passwordQuery(String passwordQuery) {
        this.configuration.setPasswordQuery(passwordQuery);
        return this;
    }

    @Override
    protected void setDefaults() {

    }

    @Override
    protected DataBaseAuthenticationConfiguration doBuild() {
        return this.configuration;
    }

}