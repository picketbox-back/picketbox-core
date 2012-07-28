package org.picketbox.core.authentication.spi;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.api.AuthenticationMechanism;
import org.picketbox.core.authentication.api.AuthenticationService;

public abstract class AbstractAuthenticationMechanism implements AuthenticationMechanism  {

    /* (non-Javadoc)
     * @see org.picketbox.core.authentication.api.AuthenticationMechanism#getService(org.picketbox.core.authentication.AuthenticationManager[])
     */
    public AuthenticationService getService(AuthenticationManager... authenticationManagers) {
        throw new IllegalStateException("Method not implemented.");
    }

}