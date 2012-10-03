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

package org.picketbox.core.authentication;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class provides informations collected during the authentication process.
 * </p>
 * <p>
 * It can be used to check the authentication status as well to get a {@link AuthenticationUser} instance.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class AuthenticationResult implements Serializable {

    private static final long serialVersionUID = -475378071790996008L;

    private transient List<String> messages = new ArrayList<String>();
    private transient AuthenticationStatus status = AuthenticationStatus.NONE;

    private Principal principal;

    public AuthenticationResult() {
    }

    public AuthenticationResult(AuthenticationStatus status) {
        this.status = status;
    }

    /**
     * @return the messages
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    /**
     * @return the status
     */
    public AuthenticationStatus getStatus() {
        return status;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
