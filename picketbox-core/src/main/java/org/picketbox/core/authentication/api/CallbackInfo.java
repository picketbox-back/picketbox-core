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

package org.picketbox.core.authentication.api;

import javax.security.auth.callback.CallbackHandler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class CallbackInfo {

    private String name;
    private String description;
    private boolean required = true;
    private Class<? extends CallbackHandler> implementation;

    public CallbackInfo(String name, String description, Class<? extends CallbackHandler> implementation) {
        this.name = name;
        this.description = description;
        this.implementation = implementation;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the implementation
     */
    public Class<? extends CallbackHandler> getImplementation() {
        return implementation;
    }
    /**
     * @param implementation the implementation to set
     */
    public void setImplementation(Class<? extends CallbackHandler> implementation) {
        this.implementation = implementation;
    }
    /**
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }
    /**
     * @param required the required to set
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

}