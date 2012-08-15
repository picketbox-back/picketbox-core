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

import java.security.Principal;
import java.util.List;

import org.picketbox.core.Credential;
import org.picketbox.core.authentication.AuthenticationInfo;
import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.AuthenticationMechanism;
import org.picketbox.core.authentication.AuthenticationProvider;
import org.picketbox.core.authentication.AuthenticationResult;
import org.picketbox.core.authentication.AuthenticationStatus;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>
 * Base class for {@link AuthenticationMechanism} implementations.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationMechanism implements AuthenticationMechanism {

    private AuthenticationProvider authenticationProvider;

    @Override
    public boolean supports(Credential credential) {
        List<AuthenticationInfo> authenticationInfo = getAuthenticationInfo();

        for (AuthenticationInfo callbackInfo : authenticationInfo) {
            if (callbackInfo.getImplementation().equals(credential.getClass())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public AuthenticationResult authenticate(Credential credential) throws AuthenticationException {
        AuthenticationResult result = new AuthenticationResult();
        return performAuthentication(result, credential);
    }

    /**
     * <p>
     * Populates the result with the informations after a successful authentication.
     * </p>
     * <p>
     * This method should provide hooks or raise events for additional processing.
     * </p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult performSuccessfulAuthentication(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.SUCCESS);
        this.authenticationProvider.getEventManager().raiseEvent(new UserAuthenticatedEvent(result));
        return result;
    }

    protected AuthenticationResult performFailedAuthentication(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.FAILED);
        this.authenticationProvider.getEventManager().raiseEvent(new UserAuthenticatedEvent(result));
        return result;
    }

    protected AuthenticationResult performAuthentication(AuthenticationResult result, Credential credential)
            throws AuthenticationException {
        Principal principal = null;

        for (AuthenticationManager authenticationManager : this.authenticationProvider.getAuthenticationManagers()) {
            if (supports(credential)) {
                try {
                    principal = doAuthenticate(authenticationManager, credential, result);
                } catch (AuthenticationException e) {
                    throw new AuthenticationException(e);
                }

                if (principal != null) {
                    break;
                }
            }
        }

        if (principal != null) {
            result.setPrincipal(principal);
            performSuccessfulAuthentication(result);
        } else {
            performFailedAuthentication(result);
        }

        return result;
    }

    protected abstract Principal doAuthenticate(AuthenticationManager authenticationManager, Credential credential,
            AuthenticationResult result) throws AuthenticationException;

    /**
     * <p>
     * Populates the result with the informations required to continue with the authentication process.
     * </p>
     * <p>
     * This method should provide hooks or raise events for additional processing.
     * </p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult requireMoreSteps(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.CONTINUE);
        return result;
    }

    /**
     * <p>
     * Populates the result with the informations after a failed authentication.
     * </p>
     * <p>
     * This method should provide hooks or raise events for additional processing.
     * </p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult authenticationFailed(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.FAILED);
        return result;
    }

    /**
     * <p>
     * Populates the result with the informations about the invalid credentials.
     * </p>
     * <p>
     * This method should provide hooks or raise events for additional processing.
     * </p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult invalidCredentials(AuthenticationResult result) {
        result.setStatus(AuthenticationStatus.INVALID_CREDENTIALS);
        return result;
    }

    protected void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}