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

import org.junit.Test;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.PicketBoxSessionListener;
import org.picketbox.core.session.PicketBoxSessionManager;

/**
 * Unit test the {@link PicketBoxSessionManager}
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class PicketBoxSessionManagerTestCase {
    @Test
    public void testMgr() throws Exception {
        PicketBoxSession session = PicketBoxSessionManager.create();
        assertNotNull(session);
        assertTrue(session.isValid());

        TestPicketBoxSessionListener listener = new TestPicketBoxSessionListener();

        session = PicketBoxSessionManager.create(listener);
        assertTrue(listener.onCreateCalled);
        assertFalse(listener.onSetAttributeCalled);
        assertFalse(listener.onInvalidateCalled);

        session.setAttribute("a", "b");
        assertTrue(listener.onSetAttributeCalled);
        assertEquals("b", session.getAttribute("a"));
        session.invalidate();
        assertFalse(session.isValid());
        assertTrue(listener.onInvalidateCalled);
    }

    private class TestPicketBoxSessionListener implements PicketBoxSessionListener {
        private boolean onCreateCalled = false;
        private boolean onSetAttributeCalled = false;
        private boolean onInvalidateCalled = false;

        @Override
        public void onCreate(PicketBoxSession session) {
            onCreateCalled = true;
        }

        @Override
        public void onSetAttribute(PicketBoxSession session, String key, Object value) {
            onSetAttributeCalled = true;
        }

        @Override
        public void onInvalidate(PicketBoxSession session) {
            onInvalidateCalled = true;
        }

        @Override
        public void onExpiration(PicketBoxSession session) {
        }
    }
}