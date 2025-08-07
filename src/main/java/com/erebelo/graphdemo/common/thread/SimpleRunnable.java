/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.thread;

/**
 * Abstract class that encapsulates simple threading functionality. This
 * runnable will continue to execute as long as the implementors of the go()
 * method return true. It will exit once a false is received or once kill9() is
 * called.
 */
public abstract class SimpleRunnable implements Runnable {

    /**
     * Flag indicating thread is alive.
     */
    private volatile boolean alive = false;

    /**
     * Continues execution until the go() method returns false or until stop() is
     * called.
     */
    @Override
    public final void run() {

        alive = true;
        while (alive && (!Thread.interrupted())) {
            alive = go();
        }
    }

    /**
     * Gracefully stops the current thread.
     */
    public final void kill9() {

        alive = false;
    }

    /**
     * Convenience method to sleep during polling to not overload the CPU or the OS
     * with native calls to check status.
     *
     * @return boolean True to continue, false if interrupted
     */
    protected static boolean sleep(final long millis) {

        try {
            Thread.sleep(millis);
            return true;
        } catch (final InterruptedException e) {
            return false;
        }
    }

    /**
     * Subclasses must implement this event, which is called by the run() method.
     * Return true to continue processing or false to exit the therad.
     *
     * @return boolean True to continue, false to stop
     */
    protected abstract boolean go();
}
