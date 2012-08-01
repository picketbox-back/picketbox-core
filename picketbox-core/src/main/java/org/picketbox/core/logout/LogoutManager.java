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

package org.picketbox.core.logout;

/**
 * <p>
 * This class provides the basic functionalities for the logout process.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public interface LogoutManager<P, Q> {

    /**
     * <p>
     * Process the logout.
     * </p>
     *
     * @param request
     * @param response
     */
    void logout(P request, Q response);

    /**
     * @return the logoutUrl
     */
    String getLogoutUrl();

    /**
     * @param logoutUrl the logoutUrl to set
     */
    void setLogoutUrl(String logoutUrl);

    /**
     * @return the logoutPage
     */
    String getLogoutPage();

    /**
     * @param logoutPage the logoutPage to set
     */
    void setLogoutPage(String logoutPage);
}