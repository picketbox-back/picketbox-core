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
public class DataBaseAuthenticationConfiguration {

    private DataSource dataSource;

    private String dsJNDIName;

    private String jpaConfigName;

    private String jpaJNDIName;

    private String passwordQuery;

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the dsJNDIName
     */
    public String getDsJNDIName() {
        return dsJNDIName;
    }

    /**
     * @param dsJNDIName the dsJNDIName to set
     */
    public void setDsJNDIName(String dsJNDIName) {
        this.dsJNDIName = dsJNDIName;
    }

    /**
     * @return the jpaConfigName
     */
    public String getJpaConfigName() {
        return jpaConfigName;
    }

    /**
     * @param jpaConfigName the jpaConfigName to set
     */
    public void setJpaConfigName(String jpaConfigName) {
        this.jpaConfigName = jpaConfigName;
    }

    /**
     * @return the jpaJNDIName
     */
    public String getJpaJNDIName() {
        return jpaJNDIName;
    }

    /**
     * @param jpaJNDIName the jpaJNDIName to set
     */
    public void setJpaJNDIName(String jpaJNDIName) {
        this.jpaJNDIName = jpaJNDIName;
    }

    /**
     * @return the passwordQuery
     */
    public String getPasswordQuery() {
        return passwordQuery;
    }

    /**
     * @param passwordQuery the passwordQuery to set
     */
    public void setPasswordQuery(String passwordQuery) {
        this.passwordQuery = passwordQuery;
    }

}
