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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.DefaultPicketBoxManager;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.session.FileSessionStore;
import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionManager;

/**
 * Unit test the {@link FileSessionStore}
 *
 * @author anil saldhana
 * @since Aug 22, 2012
 */
public class FileSessionStoreTestCase {
    
    private SessionManager sessionManager;

    @Before
    public void onSetup() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        builder.sessionManager().fileSessionStore();
        
        PicketBoxManager picketBoxManager = new DefaultPicketBoxManager(builder.build());
        
        picketBoxManager.start();
        
        this.sessionManager = picketBoxManager.getSessionManager();
    }

    @Test
    public void testStore() throws Exception {
        UserContext subject = new UserContext();

        FileSessionStore store = new FileSessionStore();
        assertNotNull(store);
        PicketBoxSession session = this.sessionManager.create(subject);
        store.store(session);
        this.sessionManager.stop();
    }

    @After
    public void tear() throws Exception {
        File file = new File("PBOXSESSION.DAT");
        assertTrue(file.exists());
        file.delete();
        assertFalse(file.exists());
    }
}