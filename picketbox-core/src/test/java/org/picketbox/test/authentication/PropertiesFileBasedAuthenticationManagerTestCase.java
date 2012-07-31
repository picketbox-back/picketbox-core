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
package org.picketbox.test.authentication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.picketbox.core.authentication.manager.PropertiesFileBasedAuthenticationManager;

/**
 * Unit test the {@link PropertiesFileBasedAuthenticationManager}
 *
 * @author anil saldhana
 * @since Jul 10, 2012
 */
public class PropertiesFileBasedAuthenticationManagerTestCase {

    @Test
    public void testAuth() throws Exception {
        PropertiesFileBasedAuthenticationManager am = new PropertiesFileBasedAuthenticationManager();
        assertNotNull(am);
        assertTrue(am.authenticate("Aladdin", "Open Sesame") != null);
        assertNull(am.authenticate("Aladdin", "Open"));
    }
}