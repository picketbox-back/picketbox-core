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

package org.picketbox.core;

import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authorization.AuthorizationManager;

/**
 * <p>This class should be used to build the configuration</p>
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class PicketBoxConfiguration {

    private static PicketBoxConfiguration instance;
    
    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;

    private PicketBoxConfiguration() {
        
    }
    
    public static PicketBoxConfiguration configure() {
        if (instance == null) {
            instance = new PicketBoxConfiguration();
        }
        
        return instance;
    }

    public PicketBoxConfiguration authentication(HTTPAuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
        return this;
    }

    public PicketBoxConfiguration authorization(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
        return this;
    }
    
    public PicketBoxManager buildAndStart() {
        PicketBoxManager securityManager = new PicketBoxManager();
        
        securityManager.setAuthenticationScheme(authenticationScheme);
        securityManager.setAuthorizationManager(authorizationManager);
        
        securityManager.start();
        
        return securityManager;
    }
    
}