/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

import com.erebelo.graphdemo.common.error.Invariant;
import com.erebelo.graphdemo.common.fp.Fn0;
import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.fp.Proc1;
import com.erebelo.graphdemo.common.lock.SimpleLock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Indicates the type is lazy loadable. The specified supplier will only be called once for the first get() and then memoize the response for
 * subsequent calls.
 */
public final class Lazy<T> {

    /**
     * Detects a recursive initialization on the same thread and instance (not static since check is per instance).
     */
    @SuppressWarnings("ThreadLocalNotStaticFinal")
    private final ThreadLocal<Boolean> initializing = ThreadLocal.withInitial(() -> false);

    /**
     * Flag indicating whether loaded (memoized) or not.
     */
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * Re-entrant lock to ensure that concurrent operations see a consistent value.
     */
    private final SimpleLock lock = SimpleLock.reentrant();

    /**
     * Supplier of the value (evaluated lazily on a call to get()).
     */
    private final Fn0<T> supplier;

    /**
     * Loader function (either retryable or fail-fast).
     */
    private final Proc1<Lazy<T>> loader;

    /**
     * Cached (memoized) value.
     */
    private T value = null;

    /**
     * Cached failure if initialization failed (only used in fail-fast mode).
     */
    private Throwable failure = null;

    /**
     * Private constructor. Use factory methods to instantiate.
     */
    private Lazy(final Fn0<T> supplier, final Proc1<Lazy<T>> loader) {

        this.supplier = supplier;
        this.loader = loader;
    }

    /**
     * Creates a lazily evaluated value that will invoke the supplied function only once, on the first call of get().
     */
    public static <T> Lazy<T> of(final Fn0<T> supplier) {

        return new Lazy<>(supplier, Lazy::loadAllowRetry);
    }

    /**
     * Creates a fail-fast lazily evaluated value that will invoke the supplied function only once, caching either the successful result or the
     * failure.
     */
    public static <T> Lazy<T> ofFailFast(final Fn0<T> supplier) {

        return new Lazy<>(supplier, Lazy::loadFailFast);
    }

    /**
     * Returns whether the type has been loaded.
     */
    public boolean loaded() {

        return loaded.get();
    }

    /**
     * Return the underlying type using the supplier originally specified.
     */
    public T get() {

        if (!loaded()) {
            load();
        }
        if (failure != null) {
            throw new IllegalStateException("Lazy initialization previously failed", failure);
        }
        return value;
    }

    /**
     * Load the value from the supplier and ensure that we do not cause deadlock or allow recursive calls to stack overflow.
     */
    private void load() {

        Invariant.require(!initializing.get(), "Recursive call to Lazy.get() on same instance in the same thread");
        lock.withVoid(() -> {
            if (!loaded.get()) {
                initializing.set(true);
                try {
                    loader.accept(this);
                } finally {
                    initializing.set(false);
                }
            }
        });
    }

    /**
     * Loads the value from the supplier and allows subsequent requests to retry if failed.
     */
    private void loadAllowRetry() {

        value = Io.withReturn(supplier);
        loaded.set(true);
    }

    /**
     * Load the value from the supplier in fail-fast mode, caching any failures.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    private void loadFailFast() {

        try {
            value = Io.withReturn(supplier);
            loaded.set(true);
        } catch (final Throwable t) {
            failure = t;
            loaded.set(true);
            throw t;
        }
    }
}
