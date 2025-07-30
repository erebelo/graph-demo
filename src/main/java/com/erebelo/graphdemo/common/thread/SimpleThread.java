/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.thread;

/**
 * SimpleThead is desigend to be used with SimpleRunnable and ThreadRegistry. The thread will retain a reference to its runnable in the registry to
 * facilitate easy, graceful shutdown of resources.
 */
@SuppressWarnings("ClassExplicitlyExtendsThread")
final class SimpleThread extends Thread {

    /**
     * Runnable associated with thread.
     */
    private final SimpleRunnable runnable;

    /**
     * Creates a simple thread.
     *
     * @param group    Registry owned thread group
     * @param runnable Runnable to execute
     * @param name     Name of the thread
     */
    SimpleThread(final ThreadGroup group, final SimpleRunnable runnable, final String name) {

        super(group, runnable, name);

        this.runnable = runnable;
    }

    /**
     * Gracefully kill the thread.
     */
    public void kill9() {

        runnable.kill9();
    }
}
