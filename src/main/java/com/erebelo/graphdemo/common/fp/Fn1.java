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
 * Wrapper to invoke an arbitrary Function that may have checked exceptions.
 */
@FunctionalInterface
public interface Fn1<T, R> {

    /**
     * Wraps the call to ensure only an unchecked exception will be thrown.
     */
    static <T, R> Function<T, R> asTry(final Fn1<? super T, ? extends R> fx) {

        return apply(fx, UnexpectedException::new);
    }

    /**
     * Wraps the call to ensure only an unchecked exception will be thrown.
     */
    static <T, R> Function<T, R> asIo(final Fn1<? super T, ? extends R> fx) {

        return apply(fx, IoException::new);
    }

    /**
     * Method taking one parameter with a return value but may throw an exception.
     */
    @SuppressWarnings("ProhibitedExceptionDeclared")
    R apply(T t) throws Exception;

    /**
     * Executes the function and throws the wraps any exception with the one
     * specified.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    private static <T, R> Function<T, R> apply(final Fn1<? super T, ? extends R> fx,
            final Function<Exception, RuntimeException> ex) {

        return t -> {
            try {
                return fx.apply(t);
            } catch (final Exception e) {
                throw ex.apply(e);
            }
        };
    }
}
