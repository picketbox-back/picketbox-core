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
package org.picketbox.authentication.http;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.PicketBoxMessages;
import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.exceptions.AuthenticationException;
import org.picketbox.util.Base64;

/**
 * Perform HTTP Client Certificate Authentication
 *
 * @author anil saldhana
 * @since July 9, 2012
 */
public class HTTPClientCertAuthentication extends AbstractHTTPAuthentication {

    /**
     * Use Certificate validation directly rather than username/cred model
     */
    protected boolean useCertificateValidation = false;

    /**
     *
     * Use Certificate validation directly rather than username/cred model. Default is false.
     *
     * @param useCertificateValidation
     */
    public void setUseCertificateValidation(boolean useCertificateValidation) {
        this.useCertificateValidation = useCertificateValidation;
    }

    /**
     * Authenticate an user
     *
     * @param servletReq
     * @param servletResp
     * @return
     * @throws AuthenticationException
     */
    public Principal authenticate(ServletRequest servletReq, ServletResponse servletResp) throws AuthenticationException {
        String username, password;

        HttpServletRequest request = (HttpServletRequest) servletReq;
        HttpServletResponse response = (HttpServletResponse) servletResp;

        X509Certificate[] certs = (X509Certificate[]) request.getAttribute(PicketBoxConstants.HTTP_CERTIFICATE);

        if (authManager == null)
            throw PicketBoxMessages.MESSAGES.invalidNullAuthenticationManager();

        if (certs != null) {
            if (useCertificateValidation) {
                return authManager.authenticate(certs);
            }

            for (X509Certificate cert : certs) {
                // Get the username
                Principal certprincipal = cert.getSubjectDN();
                if (certprincipal == null) {
                    certprincipal = cert.getIssuerDN();
                }
                if (certprincipal == null)
                    throw PicketBoxMessages.MESSAGES.unableToIdentifyCertPrincipal();

                username = certprincipal.getName();

                // Credential is the certificate
                password = Base64.encodeBytes(cert.getSignature());

                return authManager.authenticate(username, password);
            }
        }
        
        forbidClient(response);
        
        return null;
    }

    private void forbidClient(HttpServletResponse response) throws AuthenticationException {
        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException e) {
            throw new AuthenticationException(e);
        }
    }
}