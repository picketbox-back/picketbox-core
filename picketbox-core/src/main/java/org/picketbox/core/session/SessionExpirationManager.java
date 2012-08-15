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

import org.picketbox.core.config.PicketBoxConfiguration;
import org.picketbox.core.exceptions.PicketBoxSessionException;

/**
 * A manager capable of creating PicketBox sessions
 *
 * @author anil saldhana
 * @since Jul 16, 2012
 */
public class SessionExpirationManager {

    private static Timer timer = new Timer();

    private final long expiryValue;

    public SessionExpirationManager(PicketBoxConfiguration configuration) {
        this.expiryValue = configuration.getSessionManager().getSessionTimeout() * 60 * 1000;
    }

    /**
     * Set a timer for the configured delay. If the delay is <= 0 no task will be scheduled.
     *
     * @param session
     */
    void setTimer(final PicketBoxSession session) {
        if (this.expiryValue <= 0) {
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (session.isValid()) {
                    try {
                        session.expire();
                    } catch (PicketBoxSessionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, expiryValue);
    }
}