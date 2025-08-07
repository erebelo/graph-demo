/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

/**
 * Tests for various invariant conditions and throws an exception when they are
 * not true (or do not pass).
 */
@SuppressWarnings("BooleanParameter")
public final class Invariant {

    /**
     * Type contains only static members.
     */
    private Invariant() {
    }

    /**
     * Throws an IllegalStateException if the condition is not true. Use this method
     * for all non-fatal errors and all cases outside of testing.
     *
     * @param value
     *            Boolean value to test
     * @param message
     *            Error message if false
     */
    public static void require(final boolean value, final String message) {

        if (!value) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Always throws an AssertionError. This should be invoked only from areas where
     * there is unreachable code.
     *
     * @param message
     *            Message to display
     */
    public static void fail(final String message) {

        throw new AssertionError(message);
    }
}
