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
import java.util.List;

import org.picketbox.core.UserContext;
import org.picketbox.core.identity.UserContextPopulator;
import org.picketlink.idm.model.Role;
import org.picketlink.idm.model.SimpleRole;
import org.picketlink.idm.model.SimpleUser;

/**
 * <p>
 * A Simple Identity Manager that just takes in a list of roles and passes it back to the subject. Use this
 * {@link UserContextPopulator} when you have great confidence in your authentication process and all your authenticated users need
 * to get the same set of roles.
 * </p>
 *
 * @author anil saldhana
 * @since Aug 16, 2012
 */
public class ConfiguredRolesContextPopulator implements UserContextPopulator {

    private List<Role> roles = new ArrayList<Role>();

    public ConfiguredRolesContextPopulator(List<String> roles) {
        for (String string : roles) {
            this.roles.add(new SimpleRole(string));
        }
    }

    /* (non-Javadoc)
     * @see org.picketbox.core.identity.UserContextPopulator#getIdentity(org.picketbox.core.UserContext)
     */
    @Override
    public UserContext getIdentity(UserContext authenticatedUserContext) {
        authenticatedUserContext.setUser(new SimpleUser(authenticatedUserContext.getPrincipal().getName()));
        authenticatedUserContext.setRoles(this.roles);

        return authenticatedUserContext;
    }
}