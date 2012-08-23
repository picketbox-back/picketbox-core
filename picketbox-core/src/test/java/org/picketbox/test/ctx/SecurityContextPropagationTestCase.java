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
package org.picketbox.test.ctx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.security.Principal;

import org.junit.Test;
import org.picketbox.core.PicketBoxPrincipal;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.ctx.PicketBoxSecurityContext;
import org.picketbox.core.ctx.SecurityContext;
import org.picketbox.core.ctx.SecurityContextPropagation;

/**
 * Unit test the {@link SecurityContextPropagation}
 *
 * @author anil saldhana
 * @since Aug 22, 2012
 */
public class SecurityContextPropagationTestCase {
    @Test
    public void testThreadLevelPropagation() throws Exception {
        PicketBoxSubject subject = new PicketBoxSubject();

        SecurityContext sc = new PicketBoxSecurityContext(subject);

        Principal anil = new PicketBoxPrincipal("anil");
        subject.setUser(anil);

        SecurityContextPropagation.setContext(sc);

        SecurityContext retrievedCtx = SecurityContextPropagation.getContext();
        assertEquals(sc, retrievedCtx);

        assertEquals(anil, sc.getSubject().getUser());

        SecurityContextPropagation.clear();

        retrievedCtx = SecurityContextPropagation.getContext();

        assertNull(retrievedCtx);
    }

//    @Test
//    public void testSessionLevelPropagation() throws Exception {
//        SecurityContext sc = new PicketBoxSecurityContext();
//        PicketBoxSubject subject = new PicketBoxSubject();
//        sc.setSubject(subject);
//
//        Principal anil = new PicketBoxPrincipal("anil");
//        subject.setUser(anil);
//
//        SecurityContextPropagation.setContext(sc, LEVEL.SESSION);
//
//        SecurityContext retrievedCtx = SecurityContextPropagation.getContext(LEVEL.SESSION);
//        assertEquals(sc, retrievedCtx);
//
//        assertEquals(anil, sc.getSubject().getUser());
//
//        SecurityContextPropagation.clear(LEVEL.SESSION);
//        retrievedCtx = SecurityContextPropagation.getContext(LEVEL.SESSION);
//        assertNull(retrievedCtx);
//    }
//
//    @Test
//    public void testExternalSessionLevelPropagation() throws Exception {
//        PicketBoxSession session = PicketBoxSessionManager.create();
//        SecurityContext sc = new PicketBoxSecurityContext();
//        PicketBoxSubject subject = new PicketBoxSubject();
//        sc.setSubject(subject);
//
//        Principal anil = new PicketBoxPrincipal("anil");
//        subject.setUser(anil);
//
//        SecurityContextPropagation.setContext(sc, LEVEL.SESSION,session);
//
//        SecurityContext retrievedCtx = SecurityContextPropagation.getContext(LEVEL.SESSION,session);
//        assertEquals(sc, retrievedCtx);
//
//        assertEquals(anil, sc.getSubject().getUser());
//
//
//        SecurityContextPropagation.clear(LEVEL.SESSION, session);
//        retrievedCtx = SecurityContextPropagation.getContext(LEVEL.SESSION, session);
//        assertNull(retrievedCtx);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testInvalid() throws Exception {
//        PicketBoxSession session = PicketBoxSessionManager.create();
//
//        SecurityContext sc = new PicketBoxSecurityContext();
//        PicketBoxSubject subject = new PicketBoxSubject();
//        sc.setSubject(subject);
//
//        Principal anil = new PicketBoxPrincipal("anil");
//        subject.setUser(anil);
//
//        SecurityContextPropagation.setContext(sc, LEVEL.THREAD, session);
//    }
}