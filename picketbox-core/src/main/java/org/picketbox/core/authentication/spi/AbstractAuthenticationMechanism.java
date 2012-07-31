package org.picketbox.core.authentication.spi;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.api.AuthenticationMechanism;
import org.picketbox.core.authentication.api.AuthenticationProvider;
import org.picketbox.core.authentication.api.AuthenticationService;

public abstract class AbstractAuthenticationMechanism implements AuthenticationMechanism  {

    private AuthenticationProvider authenticationProvider;

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationMechanism#getService(org.picketbox.core.authentication.AuthenticationManager[])
     */
    public AuthenticationService getService(AuthenticationManager... authenticationManagers) {
        throw new IllegalStateException("Method not implemented.");
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationMechanism#getAuthenticationProvider()
     */
    @Override
    public AuthenticationProvider getAuthenticationProvider() {
        return this.authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}