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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.ldap.InitialLdapContext;

import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.authentication.AbstractAuthenticationManager;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.DigestHolder;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * An instance of {@link AuthenticationManager} that uses LDAP for authentication.
 *
 * Based on org.jboss.security.auth.spi.LdapLoginModule
 *
 * Some of the prominent options
 *
 * java.naming.provider.url= ldap://localhost:10389/
 * principalDNPrefix=   uid=
 * principalDNSuffix=   ",ou=People,dc=jboss,dc=org"
 *
 * @author Scott Stark
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class LDAPAuthenticationManager extends AbstractAuthenticationManager {
    private static final String PRINCIPAL_DN_PREFIX_OPT = "principalDNPrefix";
    private static final String PRINCIPAL_DN_SUFFIX_OPT = "principalDNSuffix";


    @Override
    public Principal authenticate(String username, Object credential) throws AuthenticationException {
        boolean isValid = false;
        try {
           // Validate the password by trying to create an initial context
           createLdapInitContext(username, credential);
           isValid = true;
        }
        catch (Throwable e) {
            throw new AuthenticationException(e);
        }
        if(isValid){
            return new PicketBoxPrincipal(username);
        }
        return null;
    }

    @Override
    public Principal authenticate(DigestHolder digest) throws AuthenticationException {
        throw new AuthenticationException("Not Implemented");
    }

    private void createLdapInitContext(String username, Object credential) throws Exception
    {
       Properties env = new Properties();
       // Map all option into the JNDI InitialLdapContext env
       Iterator<Entry<String, Object>> iter = options.entrySet().iterator();
       while (iter.hasNext()){
          Entry<String, Object> entry = iter.next();
          env.put(entry.getKey(), entry.getValue());
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
       env.setProperty(Context.SECURITY_PRINCIPAL, userDN);
       env.put(Context.SECURITY_CREDENTIALS, credential);

       InitialLdapContext ctx = null;
       try{
          ctx = new InitialLdapContext(env, null);
       }
       finally {
          // Close the context to release the connection
          if (ctx != null)
             ctx.close();
       }
    }
}