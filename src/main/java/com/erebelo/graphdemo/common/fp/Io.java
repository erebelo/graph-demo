/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.fp;

import com.erebelo.graphdemo.common.adt.Either;
import com.erebelo.graphdemo.common.error.IoException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Encapsulates operations that have side-effects (ala functional programming). This type behaves nearly identically to Try, but this type will throw
 * IoException instead of UnexpectedException.
 */
public final class Io {

    /**
     * Type contains only static members.
     */
    private Io() {
    }

    /**
     * Execute the specified function and return either any errors that occurred (left) or the result of successfuly processing (right).
     */
    public static <T> Either<Exception, T> withEither(final Fn0<T> fx) {

        return Try.withEither(fx);
    }

    /**
     * Execute the specified runnable, rethrowing any excetions encountered.
     */
    public static void withVoid(final Proc0 fx) {

        withVoid(fx, e -> {
            throw new IoException(e);
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
     * Execute the specified runnable, using the exception handler specified and a finalzier to run in all cases.
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex, final Runnable finalizer) {

        Try.withVoid(fx, ex, finalizer);
    }

    /**
     * Execute the specified supplier, rethrowing any errors that have occurred.
     */
    public static <R> R withReturn(final Fn0<R> fx) {

        return withReturn(fx, e -> {
            throw new IoException(e);
        });
    }

    /**
     * Execute the specified supplier, using the exception handler specified.
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex) {

        return Try.withReturn(fx, ex, () -> {
        });
    }

    /**
     * Execute the specified supplier, using the exception handler specified and a finalzier to run in all cases.
     */
    public static <R> R withReturn(
            final Fn0<? extends R> fx, final Function<Exception, R> ex, final Runnable finalizer) {

        return Try.withReturn(fx, ex, finalizer);
    }
}
