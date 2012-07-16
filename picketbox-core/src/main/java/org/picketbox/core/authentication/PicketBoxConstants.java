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
package org.picketbox.core.authentication;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.PicketBoxSubject;

/**
 * Define all constants
 *
 * @author anil saldhana
 * @since July 5, 2012
 */
public interface PicketBoxConstants {

    String AUTHENTICATION_KEY = "org.picketbox.authentication";
    
    String AUTH_SCHEME_LOADER = "authSchemeLoader";
    
    String AUTH_MGR = "org.picketbox.authentication.manager";
    
    String AUTHZ_MGR = "org.picketbox.authorization.manager";
    
    String BASIC = "BASIC";
    
    String CLIENT_CERT = "CLIENT_CERT";
    
    String CREDENTIAL = "picketbox.credential";
    
    String DIGEST = "DIGEST";
    
    String FORM = "FORM";

    String HTTP_AUTHORIZATION_HEADER = "Authorization";

    String HTTP_BASIC = "Basic";

    String HTTP_CERTIFICATE = "javax.servlet.request.X509Certificate";

    String HTTP_DIGEST = "Digest";

    String HTTP_DIGEST_QOP_AUTH = "auth";

    String HTTP_FORM_J_SECURITY_CHECK = "/j_security_check";

    String HTTP_FORM_J_USERNAME = "j_username";

    String HTTP_FORM_J_PASSWORD = "j_password";

    String HTTP_WWW_AUTHENTICATE = "WWW-Authenticate";

    String MD5 = "MD5";

    String PRINCIPAL = "PRINCIPAL";
    
    String SERVLET_CONTEXT = "servletContext";

    String USERS_PROPERTIES = "users.properties";

    String UTF8 = "UTF-8";

    String USERNAME = "picketbox.username";

    /**
     * <p>Defines the {@link HttpSession} attribute name used to store a {@link SavedRequest} instance.</p>
     */
    String SAVED_REQUEST = "SAVED_REQUEST";

    /**
     * <p>Defines the {@link ServletContext} attribute name used to store a {@link PicketBoxManager} instance.</p>
     */
    String PICKETBOX_MANAGER = "PICKETBOX_MANAGER";

    /**
     * <p>Defines the {@link HttpSession} attribute name used to store a {@link PicketBoxSubject} instance.</p>
     */
    String SUBJECT = "SUBJECT";
}