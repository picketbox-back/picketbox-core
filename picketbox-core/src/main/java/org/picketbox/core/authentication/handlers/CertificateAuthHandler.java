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

package org.picketbox.core.authentication.handlers;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.picketbox.core.authentication.impl.AbstractAuthenticationCallbackHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CertificateAuthHandler extends AbstractAuthenticationCallbackHandler {

    private X509Certificate[] certificates;

    public CertificateAuthHandler(X509Certificate[] certificates) {
        this.certificates = certificates;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.authentication.AuthenticationCallbackHandler#getSupportedCallbacks()
     */
    @Override
    public List<Class<? extends Callback>> getSupportedCallbacks() {
        ArrayList<Class<? extends Callback>> supportedCallbacks = new ArrayList<Class<? extends Callback>>();

        supportedCallbacks.add(CertificateCallback.class);

        return supportedCallbacks;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.picketbox.core.authentication.impl.AbstractAuthenticationCallbackHandler#doHandle(javax.security.auth.callback.Callback
     * )
     */
    @Override
    protected void doHandle(Callback callback) throws UnsupportedCallbackException {
        if (callback instanceof CertificateCallback) {
            CertificateCallback certificateCallback = (CertificateCallback) callback;

            certificateCallback.setCertificates(certificates);
        }
    }

}
