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
package org.picketbox.core.authentication.spi;

import java.util.List;

import org.picketbox.core.authentication.api.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.api.AuthenticationInfo;
import org.picketbox.core.authentication.api.AuthenticationResult;
import org.picketbox.core.authentication.api.AuthenticationService;
import org.picketbox.core.authentication.api.AuthenticationStatus;

/**
 * <p>Base class for {@link AuthenticationService} implementations.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationService implements AuthenticationService {

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationService#supportsHandler(java.lang.Class)
     */
    @Override
    public boolean supportsHandler(Class<? extends AuthenticationCallbackHandler> handlerClass) {
        List<AuthenticationInfo> authenticationInfo = getAuthenticationInfo();

        for (AuthenticationInfo callbackInfo : authenticationInfo) {
            if (callbackInfo.getImplementation().equals(handlerClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>Populates the result with the informations after a successful authentication.</p>
     * <p>This method should provide hooks or raise events for additional processing.</p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult performSuccessfulAuthentication(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.SUCCESS);
        return result;
    }

    /**
     * <p>Populates the result with the informations required to continue with the authentication process.</p>
     * <p>This method should provide hooks or raise events for additional processing.</p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult requireMoreSteps(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.CONTINUE);
        return result;
    }

    /**
     * <p>Populates the result with the informations after a failed authentication.</p>
     * <p>This method should provide hooks or raise events for additional processing.</p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult authenticationFailed(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.FAILED);
        return result;
    }

    /**
     * <p>Populates the result with the informations about the invalid credentials.</p>
     * <p>This method should provide hooks or raise events for additional processing.</p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult invalidCredentials(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.INVALID_CREDENTIALS);
        return result;
    }

}