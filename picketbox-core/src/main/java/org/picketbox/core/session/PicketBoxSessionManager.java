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
package org.picketbox.core.session;

import java.util.Timer;
import java.util.TimerTask;

import org.picketbox.core.PicketBoxMessages;

/**
 * A manager capable of creating PicketBox sessions
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class PicketBoxSessionManager {

    private static Timer timer = new Timer();

    private static long expiryValue = 5 * 60 * 1000; // 5 minutes

    /**
     * Defines the units of expiry
     */
    public static enum Expiry {
        seconds, minutes, hours
    };

    /**
     * Set the expiry
     *
     * @param milisecs
     */
    public static void setSessionExpiry(int value, Expiry type) {
        if (type == Expiry.seconds) {
            expiryValue = value * 1000;
        } else if (type == Expiry.minutes) {
            expiryValue = value * 60 * 1000;
        } else if (type == Expiry.hours) {
            expiryValue = value * 60 * 60 * 1000;
        }

    }

    /**
     * Create a new instance of {@link PicketBoxSession}
     *
     * @return
     */
    public static PicketBoxSession create() {
        PicketBoxSession session = new PicketBoxSession(new DefaultSessionKey());
        setTimer(session);
        return session;
    }

    /**
     * Create a new instance of {@link PicketBoxSession}
     *
     * @return
     */
    public static PicketBoxSession create(PicketBoxSessionListener listener) {
        PicketBoxSession session = new PicketBoxSession(new DefaultSessionKey());
        setTimer(session);
        session.addListener(listener);
        listener.onCreate(session);
        return session;
    }

    /**
     * Create a {@link PicketBoxSession}
     *
     * @param fqn Fully Qualified Class Name of a {@link PicketBoxSessionCreator}
     * @return
     * @throws {@link IllegalStateException} when instantiation of {@link PicketBoxSessionCreator} fails
     */
    public static PicketBoxSession create(String fqn) {
        if (fqn == null) {
            return create();
        }
        PicketBoxSessionCreator sessionCreator = null;
        try {
            sessionCreator = (PicketBoxSessionCreator) SecurityActions.loadClass(PicketBoxSessionManager.class, fqn)
                    .newInstance();
        } catch (Exception e) {
            throw PicketBoxMessages.MESSAGES.unableToInstantiate(fqn);
        }
        return sessionCreator.create();
    }

    /**
     * Create a {@link PicketBoxSession}
     *
     * @param fqn Fully Qualified Class Name of a {@link PicketBoxSessionCreator}
     * @param listener {@link PicketBoxSessionListener}
     * @return
     * @throws {@link IllegalStateException} when instantiation of {@link PicketBoxSessionCreator} fails
     */
    public static PicketBoxSession create(String fqn, PicketBoxSessionListener listener) {
        PicketBoxSession session = create(fqn);

        session.addListener(listener);
        listener.onCreate(session);
        return session;
    }

    /**
     * Set a timer for the configured delay
     *
     * @param session
     */
    private static void setTimer(final PicketBoxSession session) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (session.isValid()) {
                    session.expire();
                }
            }
        }, expiryValue);
    }
}