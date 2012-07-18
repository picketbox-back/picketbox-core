package org.picketbox.core;

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
     * @throws IllegalStateException if the instance was not properly started.
     */
    protected void checkIfStarted() throws IllegalStateException {
        if (!this.started()) {
            throw PicketBoxMessages.MESSAGES.instanceNotStarted();
        }
    }

}