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

package org.picketbox.test.event;

import junit.framework.Assert;

import org.junit.Test;
import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;
import org.picketbox.core.authentication.credential.UsernamePasswordCredential;
import org.picketbox.core.authentication.event.UserAuthenticatedEvent;
import org.picketbox.core.authentication.event.UserAuthenticationEventHandler;
import org.picketbox.core.config.ConfigurationBuilder;
import org.picketbox.core.event.PicketBoxEvent;
import org.picketbox.core.event.PicketBoxEventHandler;
import org.picketbox.core.event.PicketBoxEventManager;
import org.picketbox.core.logout.UserLoggedOutEvent;
import org.picketbox.core.logout.UserLoggedOutEventHandler;
import org.picketbox.test.AbstractDefaultPicketBoxManagerTestCase;

/**
 * <p>
 * Tests the {@link PicketBoxEventManager}.
 * </p>
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public class PicketBoxEventManagerTestCase extends AbstractDefaultPicketBoxManagerTestCase {

    /**
     * <p>Tests is the {@link UserAuthenticatedEvent} is properly handled when the user is successfully authenticated.</p>
     *
     * @throws Exception
     */
    @Test
    public void testSuccesfulUserAuthenticatedEvent() throws Exception {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        final StringBuffer eventStatus = new StringBuffer();

        configurationBuilder.authentication().eventManager().handler(new UserAuthenticationEventHandler() {

            @Override
            public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
                return UserAuthenticatedEvent.class;
            }

            @Override
            public void onSuccessfulAuthentication(UserAuthenticatedEvent userAuthenticatedEvent) {
                eventStatus.delete(0, eventStatus.length());
                eventStatus.append("SUCCESS");
            }

            @Override
            public void onUnSuccessfulAuthentication(UserAuthenticatedEvent userAuthenticatedEvent) {
                eventStatus.delete(0, eventStatus.length());
                eventStatus.append("FAILED");
            }

        });

        PicketBoxManager picketBoxManager = getPicketBoxManager(configurationBuilder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());
        Assert.assertEquals("SUCCESS", eventStatus.toString());
    }

    /**
     * <p>Tests is the {@link UserAuthenticatedEvent} is properly handled when the user authentication fail.</p>
     *
     * @throws Exception
     */
    @Test
    public void testUnSuccessfulUserAuthenticatedEvent() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        final StringBuffer eventStatus = new StringBuffer();

        builder.authentication().eventManager().handler(new UserAuthenticationEventHandler() {

            @Override
            public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
                return UserAuthenticatedEvent.class;
            }

            @Override
            public void onSuccessfulAuthentication(UserAuthenticatedEvent userAuthenticatedEvent) {
                eventStatus.delete(0, eventStatus.length());
                eventStatus.append("SUCCESS");
            }

            @Override
            public void onUnSuccessfulAuthentication(UserAuthenticatedEvent userAuthenticatedEvent) {
                eventStatus.delete(0, eventStatus.length());
                eventStatus.append("FAILED");
            }

        });

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "badpasswd"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        Assert.assertNotNull(subject);
        Assert.assertFalse(subject.isAuthenticated());
        Assert.assertEquals("FAILED", eventStatus.toString());
    }

    /**
     * <p>Tests is the {@link UserLoggedOutEvent} is properly handled when the user is logged out.</p>
     *
     * @throws Exception
     */
    @Test
    public void testUserLoggedOutEvent() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        final StringBuffer eventStatus = new StringBuffer();

        builder.authentication().eventManager().handler(new UserLoggedOutEventHandler() {

            @Override
            public Class<? extends PicketBoxEvent<? extends PicketBoxEventHandler>> getEventType() {
                return UserLoggedOutEvent.class;
            }

            @Override
            public void onLogOut(UserLoggedOutEvent userLogOutEvent) {
                eventStatus.append("LOGGED_OUT");
            }
        });

        PicketBoxManager picketBoxManager = getPicketBoxManager(builder.build());

        UserContext authenticatingUserContext = new UserContext();

        authenticatingUserContext.setCredential(new UsernamePasswordCredential("admin", "admin"));

        UserContext subject = picketBoxManager.authenticate(authenticatingUserContext);

        Assert.assertNotNull(subject);
        Assert.assertTrue(subject.isAuthenticated());

        picketBoxManager.logout(subject);

        Assert.assertEquals("LOGGED_OUT", eventStatus.toString());
    }

}