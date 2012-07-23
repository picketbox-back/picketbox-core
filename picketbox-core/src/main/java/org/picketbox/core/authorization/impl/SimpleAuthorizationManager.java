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
package org.picketbox.core.authorization.impl;

import java.util.ArrayList;
import java.util.List;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.authorization.AuthorizationManager;
import org.picketbox.core.authorization.Resource;
import org.picketbox.core.exceptions.AuthorizationException;

/**
 * A simple implementation of {@link AuthorizationManager} that just checks the subject against the injected role names.
 *
 * @author anil saldhana
 * @since Jul 23, 2012
 */
public class SimpleAuthorizationManager implements AuthorizationManager {

    protected List<String> roleNames = new ArrayList<String>();

    protected boolean started = false;
    protected boolean stopped = false;

    /**
     * Set the role names
     *
     * @param roleNames
     */
    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    @Override
    public boolean started() {
        return started;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public boolean stopped() {
        return stopped;
    }

    @Override
    public void stop() {
        stopped = true;
        started = false;
    }

    @Override
    public boolean authorize(Resource resource, PicketBoxSubject subject) throws AuthorizationException {
        if (stopped) {
            throw PicketBoxMessages.MESSAGES.instanceAlreadyStopped();
        }
        if (!started) {
            throw PicketBoxMessages.MESSAGES.instanceNotStarted();
        }
        List<String> subjectRoleNames = subject.getRoleNames();
        if (subjectRoleNames == null || subjectRoleNames.size() == 0) {
            return false;
        }
        for (String role : subjectRoleNames) {
            if (roleNames.contains(role)) {
                return true;
            }
        }
        return false;
    }
}