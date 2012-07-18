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

package org.picketbox.test.resource;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.picketbox.core.PicketBoxConfiguration;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authentication.PicketBoxConstants;
import org.picketbox.core.authentication.http.HTTPFormAuthentication;
import org.picketbox.core.authentication.impl.PropertiesFileBasedAuthenticationManager;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.authorization.resource.WebResource;
import org.picketbox.core.exceptions.AuthenticationException;
import org.picketbox.core.exceptions.AuthorizationException;
import org.picketbox.core.resource.ProtectedResource;
import org.picketbox.core.resource.ProtectedResourceConstraint;
import org.picketbox.test.http.TestServletContext;
import org.picketbox.test.http.TestServletContext.TestRequestDispatcher;
import org.picketbox.test.http.TestServletRequest;
import org.picketbox.test.http.TestServletResponse;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class ProtectedResourceManagerTestCase {

    private TestServletContext servletContext;
    private Resource testResource = new WebResource();

    @Before
    public void onSetup() {
        this.servletContext = new TestServletContext(new HashMap<String, String>());
    }

    @Test
    public void testNoAuthorizationResource() throws Exception {
        PicketBoxConfiguration configuration = createConfiguration();
        
        configuration.addProtectedResource(ProtectedResource.ANY_RESOURCE_PATTERN, ProtectedResourceConstraint.AUTHENTICATION);
        
        PicketBoxManager manager = configuration.buildAndStart();
        
        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        String contextPath = "/msite";
        String uri = "/anyResource";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);
        
        req.getSession().setAttribute(PicketBoxConstants.SUBJECT, new PicketBoxSubject());
        
        manager.authorize(req, resp);
        
        Assert.assertFalse(testResource.isAuthorized());
    }
    
    @Test
    public void testAuthorizationResource() throws Exception {
        PicketBoxConfiguration configuration = createConfiguration();
        
        configuration.addProtectedResource(ProtectedResource.ANY_RESOURCE_PATTERN, ProtectedResourceConstraint.AUTHORIZATION);
        
        PicketBoxManager manager = configuration.buildAndStart();
        
        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        String contextPath = "/msite";
        String uri = "/anyResource";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);

        req.getSession().setAttribute(PicketBoxConstants.SUBJECT, new PicketBoxSubject());
        
        manager.authorize(req, resp);
        
        Assert.assertTrue(testResource.isAuthorized());
    }

    @Test
    public void testResourcesProtectedWithAnyResourcePattern() throws Exception {
        PicketBoxConfiguration configuration = createConfiguration();
        
        configuration.addProtectedResource(ProtectedResource.ANY_RESOURCE_PATTERN, ProtectedResourceConstraint.ALL);
        
        PicketBoxManager manager = configuration.buildAndStart();
        
        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        String contextPath = "/msite";
        String uri = "/anyResource";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);
        
        assertLoginPage(req, resp, manager);
    }

    @Test
    public void testNotProtectedResource() throws Exception {
        PicketBoxConfiguration configuration = createConfiguration();
        
        configuration.addProtectedResource("/notProtectedResource", ProtectedResourceConstraint.NOT_PROTECTED);
        
        PicketBoxManager manager = configuration.buildAndStart();

        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        req.setMethod("GET");

        String contextPath = "/msite";
        String uri = "/notProtectedResource";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);

        // Call the server to get the digest challenge
        manager.authenticate(req, resp);

        // We will test that the request dispatcher was not used to redirect the user to the login page
        TestRequestDispatcher rd = servletContext.getLast();
        
        // user should not be redirect/forwarded to any page.
        Assert.assertNull(rd.getRequest());
    }
    
    @Test
    public void testNotProtectedPrefixedResource() throws Exception {
        PicketBoxConfiguration configuration = createConfiguration();
        
        configuration.addProtectedResource("/static/images/*", ProtectedResourceConstraint.NOT_PROTECTED);
        
        PicketBoxManager manager = configuration.buildAndStart();

        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        req.setMethod("GET");

        String contextPath = "/msite";
        String uri = "/static/images/someimage.png";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);

        // Call the server to get the digest challenge
        manager.authenticate(req, resp);

        // We will test that the request dispatcher was not used to redirect the user to the login page
        TestRequestDispatcher rd = servletContext.getLast();
        
        // user should not be redirect/forwarded to any page.
        Assert.assertNull(rd.getRequest());
    }
    
    @Test
    public void testAllResourcesProtectByDefault() throws Exception {
        PicketBoxManager manager = createConfiguration().buildAndStart();
        
        TestServletRequest req = createRequest();
        TestServletResponse resp = createResponse();

        String contextPath = "/msite";
        String uri = "/anyResource";
        
        req.setContextPath(contextPath);
        req.setRequestURI(contextPath + uri);

        assertLoginPage(req, resp, manager);
    }

    protected PicketBoxConfiguration createConfiguration() {
        return new PicketBoxConfiguration().authentication(createAuthenticationScheme()).authorization(createAuthorizationManager());
    }

    protected void assertLoginPage(TestServletRequest req, TestServletResponse resp, PicketBoxManager manager) throws AuthenticationException {
        req.setMethod("GET");

        // Original URI
        String orig = "http://msite/someurl";

        req.setRequestURI(orig);

        // Call the server to get the digest challenge
        manager.authenticate(req, resp);

        // We will test that the request dispatcher is set on the form login page
        TestRequestDispatcher rd = servletContext.getLast();
        assertEquals(rd.getRequest(), req);

        assertEquals("/login.jsp", rd.getRequestUri());
    }
    
    private TestServletResponse createResponse() {
        return new TestServletResponse(new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
            }
        });
    }

    private TestServletRequest createRequest() {
        return new TestServletRequest(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });
    }

    private HTTPFormAuthentication createAuthenticationScheme() {
        HTTPFormAuthentication authenticator = new HTTPFormAuthentication();
        
        authenticator.setAuthManager(new PropertiesFileBasedAuthenticationManager());
        authenticator.setServletContext(this.servletContext);
        
        return authenticator;
    }

    private AuthorizationManager createAuthorizationManager() {
        return new TestAuthorizationManager() {
            
            @Override
            public boolean authorize(Resource resource, PicketBoxSubject subject) throws AuthorizationException {
                testResource.setAuthorized(true);
                return true;
            }
        };
    }
    
}
