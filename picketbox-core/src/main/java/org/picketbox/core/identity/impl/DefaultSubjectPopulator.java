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

package org.picketbox.core.identity.impl;

import java.security.Principal;
import java.util.Collection;

import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.PicketBoxSubjectPopulator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class DefaultSubjectPopulator implements PicketBoxSubjectPopulator {

    private IdentityManager identityManager;

    public DefaultSubjectPopulator(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    @Override
    public PicketBoxSubject getIdentity(PicketBoxSubject authenticatedSubject) {
        if (authenticatedSubject == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("authenticatedSubject");
        }

        Principal principal = authenticatedSubject.getPrincipal();

        User userFromIDM = getIdentityManager().getUser(principal.getName());
        Collection<Role> rolesFromIDM = getIdentityManager().getRoles(userFromIDM, null);

        authenticatedSubject.setUser(userFromIDM);
        authenticatedSubject.setRoles(rolesFromIDM);

        return authenticatedSubject;
    }

    public IdentityManager getIdentityManager() {
        return this.identityManager;
    }

    public void setIdentityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }
}
