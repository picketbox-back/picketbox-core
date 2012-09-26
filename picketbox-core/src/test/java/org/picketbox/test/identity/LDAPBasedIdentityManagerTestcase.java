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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.identity.impl.LDAPBasedIdentityManager;
import org.picketbox.test.ldap.AbstractLDAPTest;

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
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.identityManager().ldapStore().url("ldap://localhost:10389/").bindDN("uid=jduke,ou=People,dc=jboss,dc=org")
                .bindCredential("theduke").userDNSuffix("ou=People,dc=jboss,dc=org").roleDNSuffix("ou=Roles,dc=jboss,dc=org");

        PicketBoxManager picketBoxManager = new DefaultPicketBoxManager(builder.build());

        picketBoxManager.start();

        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        subject = picketBoxManager.authenticate(subject);

        assertNotNull(subject);
        
        // user was loaded by the identity manager ?
        assertNotNull(subject.getUser());
        
        // TODO: LDAP Identity Store role support is being implemented
        Assert.assertTrue(subject.hasRole("Echo"));
        Assert.assertTrue(subject.hasRole("TheDuke"));
    }
}