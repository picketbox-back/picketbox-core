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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.picketbox.core.authentication.PicketBoxConstants;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class LogoutManager {

    private String logoutUrl;

    private String logoutPage;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        if (isLogoutRequest(request)) {
            HttpSession session = request.getSession(false);

            if (session == null) {
                throw new IllegalStateException("User session not created. Could not proceed with the logout.");
            }

            session.invalidate();

            try {
                String logoutPage = getLogoutPage();

                if (getLogoutPage() == null) {
                    logoutPage = request.getContextPath();
                }

                response.sendRedirect(logoutPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().contains(PicketBoxConstants.LOGOUT_URI);
    }

    /**
     * @return the logoutUrl
     */
    public String getLogoutUrl() {
        return this.logoutUrl;
    }

    /**
     * @param logoutUrl the logoutUrl to set
     */
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    /**
     * @return the logoutPage
     */
    public String getLogoutPage() {
        return this.logoutPage;
    }

    /**
     * @param logoutPage the logoutPage to set
     */
    public void setLogoutPage(String logoutPage) {
        this.logoutPage = logoutPage;
    }

}