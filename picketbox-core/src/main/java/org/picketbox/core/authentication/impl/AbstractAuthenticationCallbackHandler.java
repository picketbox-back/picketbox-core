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
package org.picketbox.core.authentication.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.picketbox.core.authentication.AuthenticationCallbackHandler;

/**
 * <p>Base class for {@link AuthenticationCallbackHandler} implementations.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationCallbackHandler implements AuthenticationCallbackHandler {

    /* (non-Javadoc)
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (!isSupported(callback)) {
                throw new UnsupportedCallbackException(callback);
            }
            doHandle(callback);
        }
    }

    private boolean isSupported(Callback callback) {
        boolean isSupported = false;

        if (getSupportedCallbacks() == null) {
            return true;
        }

        List<Class<? extends Callback>> supportedCallbacks = new ArrayList<Class<? extends Callback>>(getSupportedCallbacks());

        for (Class<? extends Callback> supportedCallback : supportedCallbacks) {
            if (supportedCallback.equals(callback.getClass())) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    protected abstract void doHandle(Callback callback) throws UnsupportedCallbackException;

}