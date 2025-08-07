/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.fp;

import com.erebelo.graphdemo.common.error.IoException;
import com.erebelo.graphdemo.common.error.UnexpectedException;
import java.util.function.Function;

/**
 * Wrapper to invoke an arbitrary Runnable that may have checked exceptions.
 */
@FunctionalInterface
public interface Proc0 {

    /**
     * Execute the specified runnable that can throw an exception. If an exception
     * is thrown, it will be converted to an unchecked exception.
     *
     * @param fx
     *            Consumer function to execute
     */
    static void runAsTry(final Proc0 fx) {

        run(fx, UnexpectedException::new);
    }

    /**
     * Execute the specified runnable that can throw an exception. If an exception
     * is thrown, it will be converted to an unchecked exception.
     *
     * @param fx
     *            Consumer function to execute
     */
    static void runAsIo(final Proc0 fx) {

        run(fx, IoException::new);
    }

    /**
     * Method taking no parameters with no return value but may throw an exception.
     */
    @SuppressWarnings("ProhibitedExceptionDeclared")
    void run() throws Exception;

    /**
     * Execute the specified runnable that can throw an exception. If an exception
     * is thrown, it will be converted to an unchecked exception.
     */
    private static <E extends RuntimeException> void run(final Proc0 fx, final Function<Exception, E> ex) {

        try {
            fx.run();
        } catch (final Exception e) {
            throw ex.apply(e);
        }
    }
}
