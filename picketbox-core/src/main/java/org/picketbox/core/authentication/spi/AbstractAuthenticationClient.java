package org.picketbox.core.authentication.spi;

import org.picketbox.core.authentication.api.AuthenticationClient;
import org.picketbox.core.authentication.api.AuthenticationStatus;

public abstract class AbstractAuthenticationClient implements AuthenticationClient {

    private AuthenticationStatus status;

    protected void performSuccessfulAuthentication() {
        this.status = AuthenticationStatus.SUCCESS;
    }

    protected void requireMoreSteps() {
        this.status = AuthenticationStatus.CONTINUE;
    }

    protected void authenticationFailed() {
        this.status = AuthenticationStatus.FAILED;
    }

    public AuthenticationStatus getStatus() {
        return this.status;
    }

}