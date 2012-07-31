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

import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.PicketBoxSubject;

/**
 * <p>This class provides informations collected during the authentication process.</p>
 * <p>It can be used to check the authentication status as well to get a {@link AuthenticationUser} instance.</p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class AuthenticationResult {

    private List<String> messages = new ArrayList<String>();
    private AuthenticationStatus status = AuthenticationStatus.NONE;
    private PicketBoxSubject subject;

    /**
     * @return the messages
     */
    public List<String> getMessages() {
        return messages;
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

    /**
     * @param status the status to set
     */
    public void setStatus(AuthenticationStatus status) {
        this.status = status;
    }

    /**
     * @return the subject
     */
    public PicketBoxSubject getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(PicketBoxSubject subject) {
        this.subject = subject;
    }

}
