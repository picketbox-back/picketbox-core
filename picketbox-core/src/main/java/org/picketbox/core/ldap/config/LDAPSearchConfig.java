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
package org.picketbox.core.ldap.config;

import javax.naming.directory.SearchControls;

/**
 * POJO that holds the information necessary for ldap search
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPSearchConfig {
    private String scope = "base";

    private String searchBase;

    private String searchFilter;

    private String searchFilterExpression;

    private Object[] filterArgs;

    private String[] searchAttributes;

    private int searchTimeLimit = 10000;

    /**
     * Get the search time limit
     *
     * @return
     */
    public int getSearchTimeLimit() {
        return searchTimeLimit;
    }

    /**
     * Set the search time limit
     *
     * @param searchTimeLimit
     */
    public void setSearchTimeLimit(int searchTimeLimit) {
        this.searchTimeLimit = searchTimeLimit;
    }

    /**
     * Get the search attributes
     *
     * @return
     */
    public String[] getSearchAttributes() {
        return searchAttributes;
    }

    /**
     * Set the search attributes
     *
     * @param searchAttributes
     */
    public void setSearchAttributes(String[] searchAttributes) {
        this.searchAttributes = searchAttributes;
    }

    /**
     * Get the base of the ldap search
     *
     * @return
     */
    public String getSearchBase() {
        return searchBase;
    }

    /**
     * Set the base of the ldap search
     *
     * @param searchBase
     */
    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    /**
     * Get the search filter
     *
     * @return
     */
    public String getSearchFilter() {
        return searchFilter;
    }

    /**
     * Set the search Filter
     *
     * @param searchFilter
     */
    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    /**
     * Search filter expression such as "member={0}"
     *
     * @return
     */
    public String getSearchFilterExpression() {
        return searchFilterExpression;
    }

    /**
     * Search filter expression such as "member={0}"
     *
     * @param searchFilterExpression
     */
    public void setSearchFilterExpression(String searchFilterExpression) {
        this.searchFilterExpression = searchFilterExpression;
    }

    /**
     * Return the scope of the search
     *
     * @return
     */
    public String getScope() {
        return scope;
    }

    /**
     * Set the scope of the search. Possible values are base, onelevel and subtree
     *
     * @param scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Get the filter arguments that need to be passed to the filter expression
     *
     * Works in conjunction with the search filter expression
     *
     * @see #setSearchFilterExpression(String)
     *
     * @return
     */
    public Object[] getFilterArgs() {
        return filterArgs;
    }

    /**
     * Set the filter arguments that need to be passed to the filter expression
     *
     * Works in conjunction with the search filter expression
     *
     * @see #setSearchFilterExpression(String)
     *
     * @param filterArgs
     */
    public void setFilterArgs(Object[] filterArgs) {
        this.filterArgs = filterArgs;
    }

    /**
     * Get the {@link SearchControls} value
     *
     * @return
     */
    public int getSearchScope() {
        if (scope.contains("one")) {
            return SearchControls.ONELEVEL_SCOPE;
        } else if (scope.contains("sub")) {
            return SearchControls.SUBTREE_SCOPE;
        }

        return SearchControls.OBJECT_SCOPE;
    }
}