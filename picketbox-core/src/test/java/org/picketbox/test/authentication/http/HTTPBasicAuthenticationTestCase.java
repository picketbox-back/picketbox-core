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
package org.picketbox.test.authentication.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.authentication.AuthenticationManager;
import org.picketbox.authentication.DigestHolder;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPBasicAuthentication;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.test.http.TestServletRequest;
import org.picketbox.test.http.TestServletResponse;
import org.picketbox.util.Base64;

/**
 * Unit test the {@link HTTPBasicAuthentication} class
 *
 * @author anil saldhana
 * @since July 5, 2012
 */
public class HTTPBasicAuthenticationTestCase {

    private HTTPBasicAuthentication httpBasic = null;

    @Before
    public void setup() throws Exception {
        httpBasic = new HTTPBasicAuthentication();

        httpBasic.setAuthManager(new AuthenticationManager() {

            @Override
            public Principal authenticate(final String username, Object credential) throws AuthenticationException {
                if ("Aladdin".equalsIgnoreCase(username) && "Open Sesame".equalsIgnoreCase((String) credential)) {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return username;
                        }
                    };
                }
                return null;
            }

            @Override
            public Principal authenticate(DigestHolder digest) throws AuthenticationException {
                return null;
            }
        });
    }

    @Test
    public void testHttpBasic() throws Exception {
        TestServletRequest req = new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        TestServletResponse resp = new TestServletResponse(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        });

        // Get Positive Authentication
        req.addHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER, "Basic " + getPositive());
        boolean result = httpBasic.authenticate(req, resp);

        assertTrue(result);

        req.clearHeaders();

        // Get Negative Authentication
        req.addHeader(PicketBoxConstants.HTTP_AUTHORIZATION_HEADER, "Basic " + getNegative());
        result = httpBasic.authenticate(req, resp);
        assertFalse(result);

        String basicHeader = resp.getHeader(PicketBoxConstants.HTTP_WWW_AUTHENTICATE);
        assertTrue(basicHeader.startsWith("basic realm="));
    }

    private String getPositive() {
        String str = "Aladdin:open sesame";
        String encoded = Base64.encodeBytes(str.getBytes());
        assertEquals("QWxhZGRpbjpvcGVuIHNlc2FtZQ==", encoded);
        return encoded;
    }

    private String getNegative() {
        String str = "Aladdin:Bad sesame";
        String encoded = Base64.encodeBytes(str.getBytes());
        return encoded;
    }
}