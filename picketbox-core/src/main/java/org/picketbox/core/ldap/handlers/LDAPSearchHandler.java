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
package org.picketbox.core.ldap.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.ldap.config.LDAPSearchConfig;

/**
 * Performs ldap search
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPSearchHandler {
    protected LDAPSearchConfig ldapSearchConfig = null;

    /**
     * Get the {@link LDAPSearchConfig}
     *
     * @return
     */
    public LDAPSearchConfig getLdapSearchConfig() {
        return ldapSearchConfig;
    }

    /**
     * Set the {@link LDAPSearchConfig}
     *
     * @param ldapSearchConfig
     */
    public void setLdapSearchConfig(LDAPSearchConfig ldapSearchConfig) {
        this.ldapSearchConfig = ldapSearchConfig;
    }

    /**
     * Execute search
     *
     * @param dc
     * @return List of string values
     */
    public List<String> executeSearch(DirContext dc) throws NamingException {
        List<String> values = new ArrayList<String>();

        if (ldapSearchConfig == null) {
            throw PicketBoxMessages.MESSAGES.ldapSearchConfigMissing();
        }
        if (ldapSearchConfig.getSearchBase() == null) {
            throw PicketBoxMessages.MESSAGES.ldapSearchBaseMissing();
        }
        SearchControls searchControl = new SearchControls();

        searchControl.setTimeLimit(ldapSearchConfig.getSearchTimeLimit());

        searchControl.setSearchScope(ldapSearchConfig.getSearchScope());
        String[] searchAttributes = ldapSearchConfig.getSearchAttributes();

        searchControl.setReturningAttributes(searchAttributes);

        String searchBase = ldapSearchConfig.getSearchBase();
        String searchFilter = ldapSearchConfig.getSearchFilter();
        String searchFilterExpression = ldapSearchConfig.getSearchFilterExpression();
        Object[] searchFilterArgs = ldapSearchConfig.getFilterArgs();

        int recursion = ldapSearchConfig.getRecursion();

        NamingEnumeration<SearchResult> ne = null;
        if (searchFilterExpression == null && searchFilter != null) {
            ne = dc.search(searchBase, searchFilter, searchControl);
        } else if (searchFilterExpression != null && searchFilterExpression.isEmpty() == false) {
            ne = dc.search(searchBase, searchFilterExpression, searchFilterArgs, searchControl);
        } else {
            ne = dc.search(searchBase, searchFilter, searchControl);
        }
        while (ne.hasMore()) {
            SearchResult result = ne.next();
            String dn = canonicalize(result.getName(), ldapSearchConfig);
            Attributes attributes = result.getAttributes();
            if (attributes != null) {
                values.addAll(getValues(attributes, searchAttributes));
            }

            while (recursion-- > 0 && ldapSearchConfig.getSearchFilterExpression() != null) {
                values.addAll(getRecursiveValues(dc, ldapSearchConfig, dn, searchControl));
            }
        }

        return values;
    }

    /**
     * Use recursion and get the attribute values
     *
     * @param dc
     * @param ldapSearchConfig
     * @param dn
     * @param searchControl
     * @return
     * @throws NamingException
     */
    private List<String> getRecursiveValues(DirContext dc, LDAPSearchConfig ldapSearchConfig, String dn,
            SearchControls searchControl) throws NamingException {
        String searchBase = ldapSearchConfig.getSearchBase();
        String searchFilterExpression = ldapSearchConfig.getSearchFilterExpression();
        Object[] searchFilterArgs = ldapSearchConfig.getFilterArgs();
        String[] searchAttributes = ldapSearchConfig.getSearchAttributes();

        List<String> values = new ArrayList<String>();

        NamingEnumeration<SearchResult> ne = null;
        if (searchFilterExpression != null && searchFilterExpression.isEmpty() == false) {
            searchFilterArgs = new Object[] { dn };
            ne = dc.search(searchBase, searchFilterExpression, searchFilterArgs, searchControl);
        }
        while (ne.hasMore()) {
            SearchResult result = ne.next();
            Attributes attributes = result.getAttributes();
            if (attributes != null) {
                values.addAll(getValues(attributes, searchAttributes));
            }
        }
        return values;
    }

    /**
     * Given a search, get the list the attribute values
     *
     * @param attributes
     * @param searchAttributes
     * @return
     * @throws NamingException
     */
    private List<String> getValues(Attributes attributes, String[] searchAttributes) throws NamingException {
        List<String> values = new ArrayList<String>();
        if (attributes != null) {
            if (searchAttributes != null) {
                for (String searchAttribute : searchAttributes) {
                    Attribute attribute = attributes.get(searchAttribute);
                    int size = attribute.size();
                    for (int i = 0; i < size; i++) {
                        values.add((String) attribute.get(i));
                    }
                }
            }
        }
        return values;
    }

    // JBAS-3438 : Handle "/" correctly
    private String canonicalize(String searchResult, LDAPSearchConfig searchConfig) {
        String base = searchConfig.getSearchBase();

        String result = searchResult;
        int len = searchResult.length();

        String appendRolesCtxDN = "" + ("".equals(base) ? "" : "," + base);
        if (searchResult.endsWith("\"")) {
            result = searchResult.substring(0, len - 1) + appendRolesCtxDN + "\"";
        } else {
            result = searchResult + appendRolesCtxDN;
        }
        return result;
    }
}