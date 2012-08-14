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

package org.picketbox.test.session;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.session.PicketBoxSession;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SessionManagerTestCase {

    private DefaultPicketBoxManager picketBoxManager;

    @Before
    public void onSetup() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.sessionManager().inMemorySessionStore();

        this.picketBoxManager = new DefaultPicketBoxManager(builder.build());
        this.picketBoxManager.start();
    }

    @Test
    public void testCreateSession() throws Exception {
        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject authenticatedSubject = this.picketBoxManager.authenticate(subject);

        Assert.assertTrue(authenticatedSubject.isAuthenticated());

        PicketBoxSession session = authenticatedSubject.getSession();

        Assert.assertNotNull(session);
    }

    @Test
    public void testInvalidateSession() throws Exception {
        PicketBoxSubject subject = new PicketBoxSubject();

        subject.setCredential(new UsernamePasswordCredential("admin", "admin"));

        PicketBoxSubject authenticatedSubject = this.picketBoxManager.authenticate(subject);

        Assert.assertTrue(authenticatedSubject.isAuthenticated());

        PicketBoxSession session = authenticatedSubject.getSession();

        Assert.assertNotNull(session);

        this.picketBoxManager.logout(authenticatedSubject);

        Assert.assertFalse(session.isValid());
    }

}
