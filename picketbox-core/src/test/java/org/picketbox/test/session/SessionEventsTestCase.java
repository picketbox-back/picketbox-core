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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;
import org.picketbox.core.session.event.SessionEvent;
import org.picketbox.core.session.event.SessionEventHandler;

/**
 * Unit test the handling of session related events.
 * 
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class SessionEventsTestCase {

    private SessionManager sessionManager;
    private TestSessionEventHandler testEventHandler;

    @Before
    public void onSetup() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        this.testEventHandler = new TestSessionEventHandler();
        
        builder
            .sessionManager()
                .fileSessionStore()
            .eventManager().handler(this.testEventHandler);

        PicketBoxManager picketBoxManager = new DefaultPicketBoxManager(builder.build());

        picketBoxManager.start();

        this.sessionManager = picketBoxManager.getSessionManager();
    }

    @Test
    public void testOnCreate() throws Exception {
        createSession();
        assertTrue(this.testEventHandler.onCreateCalled);
    }
    
    @Test
    public void testOnSetAttribute() throws Exception {
        PicketBoxSession session = createSession();

        session.setAttribute("a", "b");
        assertTrue(this.testEventHandler.onSetAttributeCalled);
        assertEquals("b", session.getAttribute("a"));
    }

    @Test
    public void testOnInvalidate() throws Exception {
        PicketBoxSession session = createSession();

        session.invalidate();
        assertFalse(session.isValid());
        assertTrue(this.testEventHandler.onInvalidateCalled);
    }
    
    @Test
    public void testOnExpire() throws Exception {
        PicketBoxSession session = createSession();

        session.expire();
        assertFalse(session.isValid());
        assertTrue(this.testEventHandler.onExpirationCalled);
    }

    @Test
    public void testGetExpire() throws Exception {
        PicketBoxSession session = createSession();
        
        session.expire();
        assertFalse(session.isValid());
        assertTrue(this.testEventHandler.onExpirationCalled);
    }


    private PicketBoxSession createSession() {
        PicketBoxSubject subject = new PicketBoxSubject();
        
        PicketBoxSession session = this.sessionManager.create(subject);

        assertNotNull(session);
        assertTrue(session.isValid());
        
        return session;
    }

    private class TestSessionEventHandler implements SessionEventHandler {
        private boolean onCreateCalled = false;
        private boolean onSetAttributeCalled = false;
        private boolean onGetAttributeCalled = false;
        private boolean onInvalidateCalled = false;
        private boolean onExpirationCalled = false;

        @Override
        public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
            return SessionEvent.class;
        }

        @Override
        public void onCreate(SessionEvent sessionEvent) {
            onCreateCalled = true;
            assertNotNull(sessionEvent);
            assertNotNull(sessionEvent.getSession());
        }

        @Override
        public void onSetAttribute(SessionEvent sessionEvent, String key, Object val) {
            onSetAttributeCalled= true;
            assertNotNull(sessionEvent);
            assertNotNull(sessionEvent.getSession());
            assertNotNull(key);
            assertNotNull(val);
        }

        @Override
        public void onGetAttribute(SessionEvent sessionEvent, String key) {
            onGetAttributeCalled = true;
            assertNotNull(sessionEvent);
            assertNotNull(sessionEvent.getSession());
            assertNotNull(key);
        }

        @Override
        public void onInvalidate(SessionEvent sessionEvent) {
            onInvalidateCalled = true;
            assertNotNull(sessionEvent);
            assertNotNull(sessionEvent.getSession());
        }

        @Override
        public void onExpiration(SessionEvent sessionEvent) {
            onExpirationCalled = true;
            assertNotNull(sessionEvent);
            assertNotNull(sessionEvent.getSession());
        }

    }
}