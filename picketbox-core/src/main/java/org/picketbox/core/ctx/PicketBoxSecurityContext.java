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
package org.picketbox.core.ctx;

import java.security.Principal;

import org.picketbox.core.PicketBoxSubject;

/**
 * Default implementation of the {@link SecurityContext}
 *
 * @author anil saldhana
 * @since Aug 22, 2012
 */
public class PicketBoxSecurityContext implements SecurityContext {
    protected PicketBoxSubject subject = null;

    public PicketBoxSecurityContext() {
    }

    public PicketBoxSecurityContext(PicketBoxSubject theSubject) {
        this.subject = theSubject;
    }

    @Override
    public PicketBoxSubject getSubject() {
        return subject;
    }

    @Override
    public Principal getPrincipal() {
        return this.subject == null ? null : this.subject.getPrincipal();
    }

    @Override
    public boolean hasRole(String role) {
        return this.subject == null ? false : this.subject.hasRole(role);
    }

}