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

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picketbox.authentication.PicketBoxConstants;
import org.picketbox.authentication.http.HTTPAuthenticationScheme;
import org.picketbox.authorization.AuthorizationManager;
import org.picketbox.authorization.resource.WebResource;
import org.picketbox.exceptions.AuthenticationException;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class PicketBoxSecurityManager implements PicketBoxLifecycle {

    private HTTPAuthenticationScheme authenticationScheme;
    private AuthorizationManager authorizationManager;
    private boolean started;
    private boolean stopped = true;
    
    PicketBoxSecurityManager() {
        
    }    
    
    public boolean isAuthenticated(HttpServletRequest servletReq, HttpServletResponse servletResp) {
        return servletReq.getSession().getAttribute(PicketBoxConstants.PRINCIPAL) != null;
    }

    public PicketBoxSubject authenticate(HttpServletRequest servletReq, HttpServletResponse servletResp) throws AuthenticationException {
        boolean authenticate = this.authenticationScheme.authenticate(servletReq, servletResp);
        
        if (!authenticate) {
            return null;
        }

        PicketBoxSubject subject = new PicketBoxSubject();
        
        subject.setUser(getAuthenticatedUser(servletReq));
        
        return subject;
    }

    public Principal getAuthenticatedUser(HttpServletRequest servletReq) {
        return (Principal) servletReq.getSession().getAttribute(PicketBoxConstants.PRINCIPAL);
    }

    @Override
    public boolean started() {
        return this.started;
    }
    
    @Override
    public void start() {
        if (this.stopped) {
            throw new IllegalStateException("PicketBox Authorization Manager was stopped.");
        }
        
        if (this.started) {
            throw new IllegalStateException("PicketBox Authorization Manager alredy started.");
        }
        
        this.started = true;
    }
    
    @Override
    public boolean stopped() {
        if (this.stopped) {
            throw new IllegalStateException("PicketBox Authorization Manager alredy stopped.");
        }
        
        return this.stopped;
    }
    
    @Override
    public void stop() {
        this.started = false;
        this.stopped = true;
    }

    public boolean authorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (this.authorizationManager == null) {
            return true;
        }
        
        WebResource resource = new WebResource();
        resource.setContext(httpRequest.getServletContext());
        resource.setRequest(httpRequest);
        resource.setResponse(httpResponse);
        
        PicketBoxSubject subject = new PicketBoxSubject();
        
        subject.setUser(getAuthenticatedUser(httpRequest));

        return this.authorizationManager.authorize(resource, subject);
    }

    /**
     * @return the authenticationScheme
     */
    public HTTPAuthenticationScheme getAuthenticationScheme() {
        return authenticationScheme;
    }

    /**
     * @param authenticationScheme the authenticationScheme to set
     */
    public void setAuthenticationScheme(HTTPAuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    /**
     * @return the authorizationManager
     */
    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }

    /**
     * @param authorizationManager the authorizationManager to set
     */
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }
    
}
