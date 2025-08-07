/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.fp;

import com.erebelo.graphdemo.common.adt.Either;
import com.erebelo.graphdemo.common.error.UnexpectedException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Semi-functional (as in functional programming, the class is fully working)
 * implementation that allows callers to safely handle code that may throw an
 * exception without using try-catch blocks themselves. This class is immutable
 * and thread-safe as it contains only static methods.
 */
public final class Try {

    /**
     * Private constructor. Type contains only static members.
     */
    private Try() {
    }

    /**
     * Execute the specified function and return either any errors that occurred
     * (left) or the result of successfully processing (right).
     */
    public static <T> Either<Exception, T> withEither(final Fn0<? extends T> fx) {

        try {
            return Either.right(fx.get());
        } catch (final Exception e) {
            return Either.left(e);
        }
    }

    /**
     * Execute the specified runnable, rethrowing any exceptions encountered.
     */
    public static void withVoid(final Proc0 fx) {

        withVoid(fx, e -> {
            throw new UnexpectedException(e);
        });
    }

    /**
     * Execute the specified runnable, using the exception handler specified.
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex) {

        withVoid(fx, ex, () -> {
        });
    }

    /**
     * Execute the specified runnable, using the exception handler specified and a
     * finalizer to run in all cases.
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex, final Runnable finalizer) {

        try {
            fx.run();
        } catch (final Exception e) {
            ex.accept(e);
        } finally {
            finalizer.run();
        }
    }

    /**
     * Execute the specified supplier, rethrowing any errors that have occurred.
     */
    public static <R> R withReturn(final Fn0<R> fx) {

        return withReturn(fx, e -> {
            throw new UnexpectedException(e);
        });
    }

    /**
     * Execute the specified supplier, using the exception handler specified.
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex) {

        return withReturn(fx, ex, () -> {
        });
    }

    /**
     * Execute the specified supplier, using the exception handler specified and a
     * finalizer to run in all cases.
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex,
            final Runnable finalizer) {

        try {
            return fx.get();
        } catch (final Exception e) {
            return ex.apply(e);
        } finally {
            finalizer.run();
        }
    }
}
