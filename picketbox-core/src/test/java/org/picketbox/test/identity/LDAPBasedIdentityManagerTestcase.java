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
package org.picketbox.test.identity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.impl.LDAPBasedIdentityManager;
import org.picketbox.test.ldap.apacheds.AbstractLDAPTest;

/**
 * Unit test the {@link LDAPBasedIdentityManager}
 *
 * @author anil saldhana
 * @since Jul 18, 2012
 */
public class LDAPBasedIdentityManagerTestcase extends AbstractLDAPTest {

    @Before
    public void setup() throws Exception {
        super.setup();
        importLDIF("ldap/users.ldif");
    }

    @Test
    public void testIdentity() throws Exception {
        LDAPBasedIdentityManager im = new LDAPBasedIdentityManager();

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("java.naming.provider.url", "ldap://localhost:10389/");
        options.put("principalDNPrefix", "uid=");
        options.put("principalDNSuffix", ",ou=People,dc=jboss,dc=org");

        options.put(Context.SECURITY_PRINCIPAL, "cn=Directory Manager");
        options.put(Context.SECURITY_CREDENTIALS, "password");
        options.put("rolesCtxDN", "ou=Roles,dc=jboss,dc=org");
        options.put("uidAttributeID", "member");
        options.put("matchOnUserDN", "true");
        options.put("roleAttributeID", "cn");
        options.put("roleAttributeIsDN", "false");

        im.setOptions(options);

        PicketBoxSubject subject = im.getIdentity(new PicketBoxPrincipal("jduke"));
        assertNotNull(subject);
        List<String> roleNames = subject.getRoleNames();
        assertTrue(roleNames != null && roleNames.size() > 0);
        assertTrue(roleNames.contains("Echo"));
        assertTrue(roleNames.contains("TheDuke"));
    }
}