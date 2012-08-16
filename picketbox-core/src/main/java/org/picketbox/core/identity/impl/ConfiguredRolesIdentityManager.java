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

import org.picketbox.core.PicketBoxSubject;
import org.picketbox.core.identity.IdentityManager;

/**
 * <p>
 * A Simple Identity Manager that just takes in a list of roles and passes it back to the subject. Use this
 * {@link IdentityManager} when you have great confidence in your authentication process and all your authenticated users need
 * to get the same set of roles.
 * </p>
 *
 * @author anil saldhana
 * @since Aug 16, 2012
 */
public class ConfiguredRolesIdentityManager implements IdentityManager {

    private List<String> roles = new ArrayList<String>();

    public ConfiguredRolesIdentityManager(List<String> roles) {
        this.roles.addAll(roles);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.identity.IdentityManager#getIdentity(org.picketbox.core.PicketBoxSubject)
     */
    @Override
    public PicketBoxSubject getIdentity(PicketBoxSubject resultingSubject) {
        resultingSubject.setRoleNames(roles);
        return resultingSubject;
    }
}