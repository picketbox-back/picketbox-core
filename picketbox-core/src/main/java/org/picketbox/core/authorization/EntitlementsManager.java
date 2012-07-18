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
package org.picketbox.core.authorization;

import org.picketbox.core.PicketBoxSubject;

/**
 * Unlike the {@link AuthorizationManager}, the {@link EntitlementsManager} is used to obtain all the entitlements with one
 * call.
 *
 * @author anil saldhana
 * @since Jul 17, 2012
 */
public interface EntitlementsManager {
    /**
     * Entitlement API
     *
     * @param resource resource for which we need to check entitlements
     * @param subject subject (user/process) that is performing an action on the resource
     * @return
     */
    Entitlement[] entitlements(Resource resource, PicketBoxSubject subject);

    /**
     * Marker interface to indicate an entitlement
     *
     * @author anil saldhana
     * @since Jul 10, 2012
     */
    public interface Entitlement {
    }
}