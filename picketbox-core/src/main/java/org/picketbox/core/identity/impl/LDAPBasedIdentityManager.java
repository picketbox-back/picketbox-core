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
package org.picketbox.core.identity.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.picketbox.core.PicketBoxLogger;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.IdentityManager;

/**
 * An instance of {@link IdentityManager} that obtains the information about an user from LDAP.
 *
 * <p/>
 * Based on org.jboss.security.auth.spi.LdapLoginModule
 * <p/>
 * Some of the prominent options
 * <p/>
 * java.naming.provider.url= ldap://localhost:10389/ principalDNPrefix uid= principalDNSuffix ",ou=People,dc=jboss,dc=org"
 *
 * @author Scott Stark
 * @author anil saldhana
 * @since Jul 17, 2012
 */
public class LDAPBasedIdentityManager implements IdentityManager {

    protected Map<String, String> options = new HashMap<String, String>();

    protected String bindDN, bindCredential;

    protected boolean initialized = false;

    private static final String PRINCIPAL_DN_PREFIX_OPT = "principalDNPrefix";
    private static final String PRINCIPAL_DN_SUFFIX_OPT = "principalDNSuffix";

    private static final String ROLES_CTX_DN_OPT = "rolesCtxDN";
    private static final String USER_ROLES_CTX_DN_ATTRIBUTE_ID_OPT = "userRolesCtxDNAttributeName";
    private static final String UID_ATTRIBUTE_ID_OPT = "uidAttributeID";
    private static final String ROLE_ATTRIBUTE_ID_OPT = "roleAttributeID";
    private static final String MATCH_ON_USER_DN_OPT = "matchOnUserDN";
    private static final String ROLE_ATTRIBUTE_IS_DN_OPT = "roleAttributeIsDN";
    private static final String ROLE_NAME_ATTRIBUTE_ID_OPT = "roleNameAttributeID";
    private static final String SEARCH_TIME_LIMIT_OPT = "searchTimeLimit";
    private static final String SEARCH_SCOPE_OPT = "searchScope";

    /**
     * Set the options
     *
     * @param options
     */
    public void setOptions(Map<String, String> options) {
        this.options.putAll(options);
        initialize();
    }

    @Override
    public PicketBoxSubject getIdentity(Principal principal) {
        PicketBoxSubject subject = new PicketBoxSubject();

        List<String> roleNames = new ArrayList<String>();

        if (initialized == false) {
            initialize();
        }
        String username = principal.getName();

        Properties env = new Properties();
        // Map all option into the JNDI InitialLdapContext env
        Iterator<Entry<String, String>> iter = options.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            env.put(entry.getKey(), entry.getValue());
        }

        if (bindDN != null) {
            // Rebind the ctx to the bind dn/credentials for the roles searches
            env.setProperty(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindCredential);

        }
        // Set defaults for key values if they are missing
        String factoryName = env.getProperty(Context.INITIAL_CONTEXT_FACTORY);
        if (factoryName == null) {
            factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
            env.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);
        }
        String authType = env.getProperty(Context.SECURITY_AUTHENTICATION);
        if (authType == null)
            env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
        String protocol = env.getProperty(Context.SECURITY_PROTOCOL);
        String providerURL = (String) options.get(Context.PROVIDER_URL);
        if (providerURL == null)
            providerURL = "ldap://localhost:" + ((protocol != null && protocol.equals("ssl")) ? "636" : "389");

        String principalDNPrefix = (String) options.get(PRINCIPAL_DN_PREFIX_OPT);
        if (principalDNPrefix == null)
            principalDNPrefix = "";
        String principalDNSuffix = (String) options.get(PRINCIPAL_DN_SUFFIX_OPT);
        if (principalDNSuffix == null)
            principalDNSuffix = "";

        String userDN = principalDNPrefix + username + principalDNSuffix;
        env.setProperty(Context.PROVIDER_URL, providerURL);

        String matchType = (String) options.get(MATCH_ON_USER_DN_OPT);
        boolean matchOnUserDN = Boolean.valueOf(matchType).booleanValue();

        InitialLdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(env, null);
        } catch (NamingException e1) {
            throw PicketBoxMessages.MESSAGES.ldapCtxConstructionFailure(e1);
        }

        /*
         * If a userRolesCtxDNAttributeName was specified, see if there is a user specific roles DN. If there is not, the
         * default rolesCtxDN will be used.
         */
        String rolesCtxDN = (String) options.get(ROLES_CTX_DN_OPT);
        String userRolesCtxDNAttributeName = (String) options.get(USER_ROLES_CTX_DN_ATTRIBUTE_ID_OPT);
        if (userRolesCtxDNAttributeName != null) {
            // Query the indicated attribute for the roles ctx DN to use
            String[] returnAttribute = { userRolesCtxDNAttributeName };
            try {
                Attributes result = ctx.getAttributes(userDN, returnAttribute);
                if (result.get(userRolesCtxDNAttributeName) != null) {
                    rolesCtxDN = result.get(userRolesCtxDNAttributeName).get().toString();
                }
            } catch (NamingException e) {
                PicketBoxLogger.LOGGER.debugFailureToQueryLDAPAttribute(userRolesCtxDNAttributeName, userDN, e);
            }
        }

        // Search for any roles associated with the user
        if (rolesCtxDN != null) {
            String uidAttrName = (String) options.get(UID_ATTRIBUTE_ID_OPT);
            if (uidAttrName == null)
                uidAttrName = "uid";
            String roleAttrName = (String) options.get(ROLE_ATTRIBUTE_ID_OPT);
            if (roleAttrName == null)
                roleAttrName = "roles";
            StringBuffer roleFilter = new StringBuffer("(");
            roleFilter.append(uidAttrName);
            roleFilter.append("={0})");
            String userToMatch = username;
            if (matchOnUserDN == true)
                userToMatch = userDN;

            String[] roleAttr = { roleAttrName };
            // Is user's role attribute a DN or the role name
            String roleAttributeIsDNOption = (String) options.get(ROLE_ATTRIBUTE_IS_DN_OPT);
            boolean roleAttributeIsDN = Boolean.valueOf(roleAttributeIsDNOption).booleanValue();

            // If user's role attribute is a DN, what is the role's name attribute
            // Default to 'name' (Group name attribute in Active Directory)
            String roleNameAttributeID = (String) options.get(ROLE_NAME_ATTRIBUTE_ID_OPT);
            if (roleNameAttributeID == null)
                roleNameAttributeID = "name";

            int searchScope = SearchControls.SUBTREE_SCOPE;
            int searchTimeLimit = 10000;
            String timeLimit = (String) options.get(SEARCH_TIME_LIMIT_OPT);
            if (timeLimit != null) {
                try {
                    searchTimeLimit = Integer.parseInt(timeLimit);
                } catch (NumberFormatException e) {
                    PicketBoxLogger.LOGGER.debugFailureToParseNumberProperty(SEARCH_TIME_LIMIT_OPT, searchTimeLimit);
                }
            }
            String scope = (String) options.get(SEARCH_SCOPE_OPT);
            if ("OBJECT_SCOPE".equalsIgnoreCase(scope))
                searchScope = SearchControls.OBJECT_SCOPE;
            else if ("ONELEVEL_SCOPE".equalsIgnoreCase(scope))
                searchScope = SearchControls.ONELEVEL_SCOPE;
            if ("SUBTREE_SCOPE".equalsIgnoreCase(scope))
                searchScope = SearchControls.SUBTREE_SCOPE;

            @SuppressWarnings("rawtypes")
            NamingEnumeration answer = null;
            try {
                SearchControls controls = new SearchControls();
                controls.setSearchScope(searchScope);
                controls.setReturningAttributes(roleAttr);
                controls.setTimeLimit(searchTimeLimit);
                Object[] filterArgs = { userToMatch };
                PicketBoxLogger.LOGGER.traceRolesDNSearch(rolesCtxDN, roleFilter.toString(), userToMatch,
                        Arrays.toString(roleAttr), searchScope, searchTimeLimit);
                answer = ctx.search(rolesCtxDN, roleFilter.toString(), filterArgs, controls);
                while (answer.hasMore()) {
                    SearchResult sr = (SearchResult) answer.next();
                    PicketBoxLogger.LOGGER.traceCheckSearchResult(sr.getName());

                    Attributes attrs = sr.getAttributes();
                    Attribute roles = attrs.get(roleAttrName);
                    if (roles != null) {
                        for (int r = 0; r < roles.size(); r++) {
                            Object value = roles.get(r);
                            String roleName = null;
                            if (roleAttributeIsDN == true) {
                                // Query the roleDN location for the value of roleNameAttributeID
                                String roleDN = value.toString();
                                String[] returnAttribute = { roleNameAttributeID };
                                PicketBoxLogger.LOGGER.traceFollowRoleDN(roleDN);
                                try {
                                    Attributes result2 = ctx.getAttributes(roleDN, returnAttribute);
                                    Attribute roles2 = result2.get(roleNameAttributeID);
                                    if (roles2 != null) {
                                        for (int m = 0; m < roles2.size(); m++) {
                                            roleName = (String) roles2.get(m);
                                            roleNames.add(roleName);
                                        }
                                    }
                                } catch (NamingException e) {
                                    PicketBoxLogger.LOGGER.debugFailureToQueryLDAPAttribute(roleNameAttributeID, roleDN, e);
                                }
                            } else {
                                // The role attribute value is the role name
                                roleName = value.toString();
                                roleNames.add(roleName);
                            }
                        }
                    } else {
                        PicketBoxLogger.LOGGER.debugFailureToFindAttrInSearchResult(roleAttrName, sr.getName());
                    }
                }
            } catch (NamingException e) {
                PicketBoxLogger.LOGGER.debugFailureToExecuteRolesDNSearch(e);
            } finally {
                if (answer != null)
                    try {
                        answer.close();
                    } catch (NamingException e) {
                        throw PicketBoxMessages.MESSAGES.namingEnumerationClose(e);
                    }
            }
        }

        subject.setRoleNames(roleNames);

        return subject;
    }

    protected void initialize() {
        bindDN = (String) options.get(Context.SECURITY_PRINCIPAL);
        bindCredential = (String) options.get(Context.SECURITY_CREDENTIALS);

        initialized = true;
    }
}