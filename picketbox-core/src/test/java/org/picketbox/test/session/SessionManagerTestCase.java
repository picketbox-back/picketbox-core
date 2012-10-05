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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.session.DefaultSessionManager;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the core functionality for the {@link DefaultSessionManager}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class SessionManagerTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    private SessionManager sessionManager;

    @Before
    public void onSetup() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder
            .sessionManager()
                .inMemorySessionStore();

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        this.sessionManager = picketBoxManager.getSessionManager();
    }

    /**
     * <p>
     * Tests if the session is properly created.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testCreateSession() throws Exception {
        PicketBoxSession session = createSession();

        assertNotNull(session);
        assertNotNull(session.getId());
        assertNotNull(session.getId().getId());
        assertNotNull(getStoredSession(session));
    }

    /**
     * <p>
     * Tests if attributes are properly stored and if {@link PicketBoxSessionListener}.onSetAttribute and onGetAttribute methods
     * are properly called.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testOnSetAndGetAttribute() throws Exception {
        PicketBoxSession session = createSession();

        session.setAttribute("test", "test");

        PicketBoxSession storedSession = getStoredSession(session);

        assertNotNull(storedSession.getAttribute("test"));
        assertEquals("test", storedSession.getAttribute("test"));
    }

    /**
     * <p>
     * Tests if the the session is properly invalidated and the {@link PicketBoxSessionListener}.onInvalidate method is properly
     * called.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSessionInvalidation() throws Exception {
        PicketBoxSession session = createSession();

        session.invalidate();

        assertFalse(session.isValid());

        Assert.assertNull(getStoredSession(session));
    }

    /**
     * <p>
     * Tests if the the session is properly expired and the {@link PicketBoxSessionListener}.onExpiration method is properly
     * called.
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testSessionExpiration() throws Exception {
        PicketBoxSession session = createSession();

        session.expire();

        assertFalse(session.isValid());

        Assert.assertNull(getStoredSession(session));
    }

    private PicketBoxSession createSession() {
        PicketBoxSubject subject = new PicketBoxSubject() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isAuthenticated() {
                return true;
            }
        };

        return this.sessionManager.create(subject);
    }

    private PicketBoxSession getStoredSession(PicketBoxSession session) {
        return this.sessionManager.retrieve(session.getId());
    }

}