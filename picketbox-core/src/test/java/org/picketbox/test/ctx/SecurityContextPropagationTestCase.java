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
import org.picketbox.core.UserContext;
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
    
	@SuppressWarnings("serial")
	@Test
    public void testThreadLevelPropagation() throws Exception {
        final Principal anil = new PicketBoxPrincipal("anil");

        UserContext subject = new UserContext() {
            @Override
            public Principal getPrincipal() {
                return anil;
            }
        };

        SecurityContext sc = new PicketBoxSecurityContext(subject);
        SecurityContextPropagation.setContext(sc);

        SecurityContext retrievedCtx = SecurityContextPropagation.getContext();
        assertEquals(sc, retrievedCtx);

        assertEquals(anil, sc.getUserContext().getPrincipal());

        SecurityContextPropagation.clear();

        retrievedCtx = SecurityContextPropagation.getContext();

        assertNull(retrievedCtx);
    }

}