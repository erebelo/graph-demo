/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.thread;

/**
 * Singleton instance of threads created using this package. Provides
 * convenience and safety mechanisms to ensure all threads registered can be
 * safely stopped. Also applies a standard naming convention to threads and
 * ensures they are from this package.
 */
public enum ThreadRegistry {

    /**
     * Singleton instance.
     */
    INSTANCE;

    /**
     * Sleep interval.
     */
    private static final int SLEEP_INTERVAL = 1000;

    /**
     * Master thread group.
     */
    private final ThreadGroup rootGroup = new ThreadGroup(SimpleRunnable.class + ".RootGroup");

    /**
     * Registers a thread and returns it properly populated.
     *
     * @param runnable
     *            Runnable to use
     * @return Thread Thread registered here
     */
    public synchronized Thread register(final SimpleRunnable runnable) {

        return new SimpleThread(rootGroup, runnable, getClass().getName() + ':' + runnable.getClass().getName());
    }

    /**
     * Stop all running threads. This will first attempt to shut each down
     * gracefully. Then if any are still active, they will be forceibly interrupted.
     * If this method is itself interrupted, then the assumption is a SIGTERM or
     * something similar already fired, and resources will be left in an
     * inconsistent state.
     */
    public synchronized void kill9() {

        // First attempt to shutdown gracefully
        shutdownGracefully();
        // Force any laggards to quit
        rootGroup.interrupt();
    }

    /**
     * Helper method to attempt to gracefully stop all active threads.
     */
    @SuppressWarnings("ReassignedVariable")
    private void shutdownGracefully() {

        // Get an array of all active threads in the group
        final var threads = new Thread[rootGroup.activeCount()];
        final var numThreads = rootGroup.enumerate(threads);

        // Attempt to shut each down gracefully
        for (var i = 0; i < numThreads; i++) {
            final var current = (SimpleThread) threads[i];
            current.kill9();
        }
    }
}
