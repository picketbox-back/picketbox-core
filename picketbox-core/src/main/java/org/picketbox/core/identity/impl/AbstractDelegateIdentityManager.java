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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.picketlink.idm.internal.DefaultIdentityManager;
import org.jboss.picketlink.idm.model.Role;
import org.jboss.picketlink.idm.spi.IdentityStore;
import org.picketbox.core.PicketBoxMessages;
import org.picketbox.core.identity.DefaultRole;
import org.picketbox.core.identity.DefaultUser;
import org.picketbox.core.identity.IdentityManager;
import org.picketbox.core.identity.User;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public abstract class AbstractDelegateIdentityManager implements IdentityManager {

    private DefaultIdentityManager delegateIdentityManager;

    @Override
    public User getIdentity(String userName) {
        if (userName == null) {
            throw PicketBoxMessages.MESSAGES.invalidNullArgument("userName");
        }

        org.jboss.picketlink.idm.model.User userFromIDM = getDelegateIdentityManager().getUser(userName);
        Collection<Role> rolesFromIDM = getDelegateIdentityManager().getRoles(userFromIDM, null);

        List<org.picketbox.core.identity.Role> roles = new ArrayList<org.picketbox.core.identity.Role>();

        for (Role role : rolesFromIDM) {
            roles.add(new DefaultRole(role.getName()));
        }

        DefaultUser picketboxUser = new DefaultUser(userName, roles);

        return picketboxUser;
    }

    private DefaultIdentityManager getDelegateIdentityManager() {
        if (this.delegateIdentityManager == null) {
            this.delegateIdentityManager = new DefaultIdentityManager();

            this.delegateIdentityManager.setIdentityStore(createIdentityStore());
        }

        return this.delegateIdentityManager;
    }

    protected abstract IdentityStore createIdentityStore();

}
