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

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.picketbox.core.session.PicketBoxSession;
import org.picketbox.core.session.SessionId;

/**
 * An Application View of the authenticated/authorized Subject
 *
 * @author anil saldhana
 * @since Jul 12, 2012
 */
public class PicketBoxSubject implements Serializable {

    private static final long serialVersionUID = -7767959770091515534L;

    protected Subject subject;
    protected Principal user;
    protected List<String> roleNames = new ArrayList<String>();
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    protected transient Map<String, Object> contextData = new HashMap<String, Object>();

    private boolean authenticated;

    private transient PicketBoxSession session;

    private transient Credential credential;

    // TODO: how to deal with groups/nested groups etc

    public PicketBoxSubject() {

    }

    public PicketBoxSubject(SessionId<? extends Serializable> sessionId) {
        this.session = new PicketBoxSession(sessionId);
    }

    /**
     * get the user
     *
     * @return
     */
    public Principal getUser() {
        return user;
    }

    /**
     * Set the user
     *
     * @param user
     */
    public void setUser(Principal user) {
        this.user = user;
    }

    /**
     * Get the role names
     *
     * @return
     */
    public List<String> getRoleNames() {
        return Collections.unmodifiableList(roleNames);
    }

    /**
     * Set the role names of the user
     *
     * @param rolesNames
     */
    public void setRoleNames(List<String> rolesNames) {
        this.roleNames.addAll(rolesNames);
    }

    /**
     * Get the user attributes
     *
     * @return
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Set the attributes
     *
     * @param attributes
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }

    /**
     * Get the JAAS Subject if available
     *
     * @return
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Set the JAAS Subject
     *
     * @param subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Get a read only map of contextual data
     *
     * @return
     */
    public Map<String, Object> getContextData() {
        return Collections.unmodifiableMap(contextData);
    }

    /**
     * Set context data
     *
     * @param contextData
     */
    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    protected void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    /**
     * @return
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setSession(PicketBoxSession session) {
        this.session = session;
    }

    public PicketBoxSession getSession() {
        return session;
    }

    public Credential getCredential() {
        return this.credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public void invalidate() {
        this.authenticated = false;
        this.credential = null;
        this.contextData.clear();
        this.roleNames.clear();
        this.user = null;
    }

    /**
     * <p>Checks if this subject has the specified role.</p>
     *
     * @param role
     * @return
     */
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            throw PicketBoxMessages.MESSAGES.userNotAuthenticated();
        }

        for (String userRole : this.roleNames) {
            if (userRole.equals(role)) {
                return true;
            }
        }

        return false;
    }
}