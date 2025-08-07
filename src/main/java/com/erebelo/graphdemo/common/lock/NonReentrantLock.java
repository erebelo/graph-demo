/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.lock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.jetbrains.annotations.NotNull;

/**
 * Fair lock that is non re-entrant. If the same thread tries to lock() twice,
 * it will deadlock rather than recurse.
 */
final class NonReentrantLock implements Lock {

    private final Semaphore sem = new Semaphore(1, true);

    @Override
    public void lock() {

        sem.acquireUninterruptibly();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

        sem.acquire();
    }

    @Override
    public boolean tryLock() {

        return sem.tryAcquire();
    }

    @Override
    public boolean tryLock(final long time, final @NotNull TimeUnit unit) throws InterruptedException {

        return sem.tryAcquire(time, unit);
    }

    @Override
    public void unlock() {
        sem.release();
    }

    @Override
    public @NotNull Condition newCondition() {

        throw new UnsupportedOperationException("Lock does not support new conditions");
    }
}
