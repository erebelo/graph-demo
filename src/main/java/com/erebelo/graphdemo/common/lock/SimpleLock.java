/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.lock;

import com.erebelo.graphdemo.common.fp.Fn0;
import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.fp.Proc0;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Functional implementation concurrent locks.
 */
public final class SimpleLock {

    /**
     * Underlying re-entrant lock.
     */
    private final Lock lock;

    /**
     * Creates a simple lock wrapper for the specified lock.
     *
     * @param lock Lock to wrap
     */
    private SimpleLock(final Lock lock) {

        this.lock = lock;
    }

    /**
     * Returns a re-entrant lock, with similar semantics to the Java synchronized keyword. This implementation by default utilizes a fair lock,
     * whereby the thread with the longest wait gets first access to the lock.
     */
    public static SimpleLock reentrant() {

        return new SimpleLock(new ReentrantLock(true));
    }

    public static @NotNull SimpleLock nonReentrant() {

        return new SimpleLock(new NonReentrantLock());
    }

    /**
     * Executes the special function within a re-entrant lock.
     */
    public <T> T withReturn(final @NotNull Fn0<T> fx) {

        lock.lock();
        try {
            return Io.withReturn(fx);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Executes the special function within a re-entrant lock.
     */
    public void withVoid(final @NotNull Proc0 fx) {

        lock.lock();
        try {
            Io.withVoid(fx);
        } finally {
            lock.unlock();
        }
    }
}
