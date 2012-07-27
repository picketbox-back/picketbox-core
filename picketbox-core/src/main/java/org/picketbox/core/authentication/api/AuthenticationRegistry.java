package org.picketbox.core.authentication.api;

import java.util.Map;

interface AuthenticationRegistry {

    Map<String, String> allProviders();

}