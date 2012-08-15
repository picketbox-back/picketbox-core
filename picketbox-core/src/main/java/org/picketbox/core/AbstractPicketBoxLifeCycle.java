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
package org.picketbox.core;

/**
 * Base class for lifecycle
 *
 * @author anil saldhana
 */
public abstract class AbstractPicketBoxLifeCycle implements PicketBoxLifecycle {

    /*
     * Life cycle attributes.
     */
    private boolean started;
    private boolean stopped = true;

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#started()
     */
    @Override
    public boolean started() {
        return this.started;
    }

    @Override
    public void start() {
        if (this.started) {
            throw PicketBoxMessages.MESSAGES.instanceAlreadyStarted();
        }

        doStart();

        this.started = true;
        this.stopped = false;
    }

    protected abstract void doStart();

    @Override
    public void stop() {
        if (this.stopped) {
            throw PicketBoxMessages.MESSAGES.instanceAlreadyStopped();
        }

        doStop();

        this.started = false;
        this.stopped = true;
    }

    protected abstract void doStop();

    /*
     * (non-Javadoc)
     *
     * @see org.picketbox.core.PicketBoxLifecycle#stopped()
     */
    @Override
    public boolean stopped() {
        return this.stopped;
    }

    /**
     * <p>
     * Checks if the manager is started.
     * </p>
     *
     * @throws IllegalStateException if the instance was not properly started.
     */
    protected void checkIfStarted() throws IllegalStateException {
        if (!this.started()) {
            throw PicketBoxMessages.MESSAGES.instanceNotStarted();
        }
    }
}