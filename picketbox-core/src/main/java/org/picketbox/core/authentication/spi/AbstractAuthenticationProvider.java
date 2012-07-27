package org.picketbox.core.authentication.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.picketbox.core.authentication.api.AuthenticationMechanism;
import org.picketbox.core.authentication.api.SecurityException;

public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    private final Map<String, AuthenticationMechanism> mechanisms = new HashMap<String, AuthenticationMechanism>();

    public AbstractAuthenticationProvider() {
        super();
    }

    public void initialize() {
        doAddMechanisms(this.mechanisms);
    }

    protected abstract void doAddMechanisms(Map<String, AuthenticationMechanism> mechanisms);

    public String[] getSupportedMechanisms() {
        Set<Entry<String, AuthenticationMechanism>> entrySet = this.mechanisms.entrySet();

        String[] mechanisms = new String[entrySet.size()];

        int i = 0;

        for (Entry<String, AuthenticationMechanism> entry : entrySet) {
            mechanisms[i++] = entry.getKey();
        }

        return mechanisms;
    }

    public boolean supports(String mechanismName) {
        return this.mechanisms.containsKey(mechanismName);
    }

    public AuthenticationMechanism getMechanism(String mechanismName) {
        if (!supports(mechanismName)) {
            throw new SecurityException("No mechanism found for '" + mechanismName + "'. Possible mechanisms are: " + getSupportedMechanisms());
        }

        return this.mechanisms.get(mechanismName);
    }

}