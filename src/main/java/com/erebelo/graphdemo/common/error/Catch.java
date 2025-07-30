/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class to assist with handling caught exceptions properly.
 */
@SuppressWarnings("ChainOfInstanceofChecks")
public final class Catch {

    /**
     * Type contains only static members.
     */
    private Catch() {
    }

    /**
     * Executes the specified handler. This is normally invoked from within a catch-block.
     */
    public static void withVoid(final Throwable thrown, final Consumer<Throwable> fx) {

        try {
            fx.accept(thrown);
            if (requiresRethrow(thrown)) {
                rethrow(thrown);
            }
        } catch (final Exception chained) {
            thrown.addSuppressed(chained);
            rethrow(thrown);
        }
    }

    /**
     * Executes the specified funciton, returning the result. This is normally called from within a catch-block.
     */
    public static <T> T withReturn(final Throwable thrown, final Function<Throwable, T> fx) {

        try {
            final var result = fx.apply(thrown);
            if (requiresRethrow(thrown)) {
                rethrow(thrown);
            }
            return result;
        } catch (final Exception chained) {
            thrown.addSuppressed(chained);
            rethrow(thrown);
            throw new IllegalStateException("Unreachable code", chained);
        }
    }

    /**
     * Determines whether a re-throw is required.
     */
    private static boolean requiresRethrow(final Throwable thrown) {

        return !(thrown instanceof Exception);
    }

    /**
     * Safely re-throws an exception based on its instance type.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    private static void rethrow(final Throwable thrown) {

        if (thrown instanceof Error) {
            throw (Error) thrown;
        }
        if (thrown instanceof RuntimeException) {
            throw (RuntimeException) thrown;
        }
        throw new UnexpectedException("Illegal instance of throwable", thrown);
    }
}
