/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

import com.erebelo.graphdemo.common.fp.Proc0;

import java.io.Serial;

/**
 * Various strategies for retrying an operation.
 */
public final class Retry {

    /**
     * Private constructor. Class contains only static methods.
     */
    private Retry() {
    }

    /**
     * Simple retry policy that attempts the specified number of retries (including the first attempt) while waiting the indicated number of
     * milliseconds in between.
     *
     * @param run        Command to run
     * @param count      Number of attempts
     * @param waitMillis Time between attempts
     */
    public static void simple(final Proc0 run, final int count, final long waitMillis) {

        final var e = new RetryLimitExceededException("Failed after " + count + " attempts");
        final var success = invokeSimple(run, count, waitMillis, e);
        if (!success) {
            throw e;
        }
    }

    /**
     * Recursive method to continually retry in fixed time increments.
     *
     * @param fx         Command to run
     * @param count      Downward count to zero on remaining retries
     * @param waitMillis Milliseconds to wait
     * @param e          Exception placeholder for any errors
     * @return boolean True if successful, false if an exception occurred
     */
    private static boolean invokeSimple(final Proc0 fx, final int count, final long waitMillis, final Exception e) {

        if (count == 0) {
            return false;
        }
        try {
            fx.run();
            return true;
        } catch (final Exception ex) {
            ex.addSuppressed(e);
        }
        try {
            Thread.sleep(waitMillis);
        } catch (final InterruptedException ex) {
            e.addSuppressed(ex);
        }
        return invokeSimple(fx, count - 1, waitMillis, e);
    }

    /**
     * Exception thrown when retry limit exceeded.
     */
    public static final class RetryLimitExceededException extends RuntimeException {

        /**
         * Serialization constant.
         */
        @Serial
        private static final long serialVersionUID = -6834446319152512294L;

        /**
         * Creates an exception.
         *
         * @param message Message to display
         */
        public RetryLimitExceededException(final String message) {

            super(message);
        }
    }
}
