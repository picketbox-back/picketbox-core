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

import java.security.Principal;
import java.util.List;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.api.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.api.AuthenticationEvent;
import org.picketbox.core.authentication.api.AuthenticationEventHandler;
import org.picketbox.core.authentication.api.AuthenticationEventManager;
import org.picketbox.core.authentication.api.AuthenticationInfo;
import org.picketbox.core.authentication.api.AuthenticationProviderFactory;
import org.picketbox.core.authentication.api.AuthenticationResult;
import org.picketbox.core.authentication.api.AuthenticationService;
import org.picketbox.core.authentication.api.AuthenticationStatus;
import org.picketbox.core.authentication.api.AuthenticationUser;
import org.picketbox.core.authentication.api.SecurityRealm;
import org.picketbox.core.exceptions.AuthenticationException;

/**
 * <p>Base class for {@link AuthenticationService} implementations.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractAuthenticationService implements AuthenticationService {

    private AuthenticationEventManager eventManager = new DefaultAuthenticationEventManager();

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

    @Override
    public AuthenticationResult authenticate(AuthenticationCallbackHandler callbackHandler) throws AuthenticationException {
        AuthenticationResult result = new AuthenticationResult();
        return performAuthentication(result, callbackHandler);
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationService#authenticate(java.lang.String, org.picketbox.core.authentication.api.AuthenticationCallbackHandler)
     */
    public AuthenticationResult authenticate(String realm, AuthenticationCallbackHandler callbackHandler)
            throws AuthenticationException {
        return null;
    }

    /**
     * <p>Populates the result with the informations after a successful authentication.</p>
     * <p>This method should provide hooks or raise events for additional processing.</p>
     *
     * @param result
     * @return
     */
    protected AuthenticationResult performSuccessfulAuthentication(AuthenticationResult result) {
        result.getUser().setAuthenticated(true);
        result.setStatus(AuthenticationStatus.SUCCESS);
        this.eventManager.raiseEvent(new UserAuthenticatedEvent(result));
        return result;
    }

    protected AuthenticationResult performAuthentication(AuthenticationResult result, AuthenticationCallbackHandler callbackHandler) throws AuthenticationException {
        SecurityRealm defaultRealm = AuthenticationProviderFactory.instance().getDefaultRealm();

        List<AuthenticationManager> authenticationManagers = defaultRealm.getAuthenticationManagers();

        Principal principal = null;

        for (AuthenticationManager authenticationManager : authenticationManagers) {
            if (supportsHandler(callbackHandler.getClass())) {
                try {
                    principal = doAuthenticate(authenticationManager, callbackHandler, result);
                } catch (AuthenticationException e) {
                    throw new AuthenticationException(e);
                }

                if (principal != null) {
                    break;
                }
            }
        }

        if (principal != null) {
            AuthenticationUser authenticatedUser = new AuthenticationUser();

            authenticatedUser.setPrincipal(principal);

            result.setAuthenticatedUser(authenticatedUser);

            performSuccessfulAuthentication(result);
        } else {
            invalidCredentials(result);
        }

        return result;
    }

    protected abstract Principal doAuthenticate(AuthenticationManager authenticationManager, AuthenticationCallbackHandler callbackHandler, AuthenticationResult result) throws AuthenticationException;

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

    @Override
    public void addObserver(Class<? extends AuthenticationEvent> eventType, AuthenticationEventHandler handler) {
        this.eventManager.addHandler(eventType, handler);
    }
}