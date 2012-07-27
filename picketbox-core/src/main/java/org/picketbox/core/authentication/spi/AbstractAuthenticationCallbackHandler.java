package org.picketbox.core.authentication.spi;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.picketbox.core.authentication.AuthenticationManager;
import org.picketbox.core.authentication.api.AuthenticationCallbackHandler;
import org.picketbox.core.authentication.api.CredentialValidationCallback;
import org.picketbox.core.authentication.api.DigestCredentialValidationCallback;
import org.picketbox.core.authentication.api.SecurityException;
import org.picketbox.core.exceptions.AuthenticationException;

public abstract class AbstractAuthenticationCallbackHandler implements AuthenticationCallbackHandler {

    private AuthenticationManager authManager;

    public AbstractAuthenticationCallbackHandler(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    /* (non-Javadoc)
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (!isSupported(callback)) {
                throw new UnsupportedCallbackException(callback);
            }

            doHandle(callback);
            performCredentialsVailidation(callback);
        }
    }

    private boolean isSupported(Callback callback) {
        boolean isSupported = false;

        if (getSupportedCallbacks() == null) {
            return true;
        }

        List<Class<? extends Callback>> supportedCallbacks = new ArrayList<Class<? extends Callback>>(getSupportedCallbacks());

        supportedCallbacks.add(CredentialValidationCallback.class);

        for (Class<? extends Callback> supportedCallback : supportedCallbacks) {
            if (supportedCallback.equals(callback.getClass())) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    private void performCredentialsVailidation(Callback callback) {
        if (callback instanceof CredentialValidationCallback) {
            CredentialValidationCallback credentialValidationCallback = (CredentialValidationCallback) callback;

            try {
                Principal authenticate = this.authManager.authenticate(credentialValidationCallback.getUserName(), credentialValidationCallback.getCredential().toString());

                credentialValidationCallback.setPrincipal(authenticate);
            } catch (AuthenticationException e) {
                throw new SecurityException("Error validating user's credentials.");
            }
        }

        if (callback instanceof DigestCredentialValidationCallback) {
            DigestCredentialValidationCallback credentialValidationCallback = (DigestCredentialValidationCallback) callback;

            try {
                Principal authenticate = this.authManager.authenticate(credentialValidationCallback.getDigestInfo());

                credentialValidationCallback.setPrincipal(authenticate);
            } catch (AuthenticationException e) {
                throw new SecurityException("Error validating user's credentials.");
            }
        }
    }

    protected abstract void doHandle(Callback callback) throws UnsupportedCallbackException;

}