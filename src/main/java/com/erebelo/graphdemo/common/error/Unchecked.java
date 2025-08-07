/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

/**
 * Utility to allow throwing checked exceptions as unchecked. Relies on the fact
 * that generics are erased at runtime.
 */
final class Unchecked {

    /**
     * Private constructor. Class has only static methods.
     */
    private Unchecked() {
    }

    /**
     * Convert any exception to an unchecked exception and rethrow it. This method
     * returns a RuntimeException that will (should) never be actually returned. The
     * logic is to allow calls like: <br>
     * {@code catch (final Exception e) { throw rethrow(e); } } <br>
     * Otherwise, the compiler will complain about a catch block that is not
     * returning anything if the declaring method is anything other than void.
     *
     * @param t
     *            Exception to rethrow as unchecked
     * @return UnexpectedException Should never be returned
     */
    @SuppressWarnings({"RedundantTypeArguments", "ProhibitedExceptionThrown"})
    public static UnexpectedException rethrow(final Exception t) {

        final var cause = getRootCause(t);
        if (cause instanceof final RuntimeException rt) {
            throw rt;
        }
        Unchecked.<RuntimeException>coerceAndRethrow(cause);
        return new UnexpectedException("Unreachable code", t);
    }

    /**
     * Returns the promximate cause (closest stack trace to the error).
     */
    private static Throwable getRootCause(final Throwable t) {

        final var cause = t.getCause();
        return (cause == null) ? t : getRootCause(cause);
    }

    /**
     * Helper method to type erase the exception.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void coerceAndRethrow(final Throwable t) throws T {

        throw (T) t;
    }
}
