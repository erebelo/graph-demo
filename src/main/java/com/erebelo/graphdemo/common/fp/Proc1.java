/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.fp;

import com.erebelo.graphdemo.common.error.IoException;
import com.erebelo.graphdemo.common.error.UnexpectedException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Wrapper to invoke an arbitrary Consumer that may have checked exceptions.
 */
@FunctionalInterface
public interface Proc1<T> {

    /**
     * Wraps the call to ensure only an unchecked exception will be thrown.
     */
    static <T> Consumer<T> asTry(final Proc1<? super T> c) {

        return accept(c, UnexpectedException::new);
    }

    /**
     * Wraps the call to ensure only an unchecked exception will be thrown.
     */
    static <T> Consumer<T> asIo(final Proc1<? super T> c) {

        return accept(c, IoException::new);
    }

    /**
     * Method taking one parameter with no return value but may throw an exception.
     */
    @SuppressWarnings("ProhibitedExceptionDeclared")
    void accept(T t) throws Exception;

    /**
     * Consistently execute the throwable consumer passed in and generate a mapped
     * exception.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    private static <T> Consumer<T> accept(final Proc1<? super T> c, final Function<Exception, RuntimeException> ex) {

        return t -> {
            try {
                c.accept(t);
            } catch (final Exception e) {
                throw ex.apply(e);
            }
        };
    }
}
