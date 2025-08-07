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
 * Wrapper to invoke an arbitrary Supplier that may have checked exceptions.
 */
@FunctionalInterface
public interface Fn0<T> {

    /**
     * Execute the specified supplier that can throw an exception. If an exception
     * is thrown, it will be converted to an unchecked exception.
     *
     * @param fx
     *            Supplier function to execute
     * @param <T>
     *            Type parameter of consumer arguent
     * @return T Value returned
     */
    static <T> T asTry(final Fn0<T> fx) {

        return get(fx, UnexpectedException::new);
    }

    /**
     * Execute the specified supplier that can throw an exception. If an exception
     * is thrown, it will be converted to an unchecked exception.
     *
     * @param fx
     *            Supplier function to execute
     * @param <T>
     *            Type parameter of consumer arguent
     * @return T Value returned
     */
    static <T> T asIo(final Fn0<T> fx) {

        return get(fx, IoException::new);
    }

    /**
     * Method that returns a value possibly throwing an exception.
     */
    @SuppressWarnings("ProhibitedExceptionDeclared")
    T get() throws Exception;

    /**
     * Execute the specified supplier that can throw an exception.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    private static <T> T get(final Fn0<? extends T> fx, final Function<Exception, RuntimeException> ex) {

        try {
            return fx.get();
        } catch (final Exception e) {
            throw ex.apply(e);
        }
    }
}
