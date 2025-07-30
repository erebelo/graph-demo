/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.collection;

import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper methods to work with a java.util.Stream when the normal API is confusing or difficult.
 */
public final class Streams {

    /**
     * Type contains only static methods.
     */
    private Streams() {
    }

    /**
     * Converts a byte array to a stream.
     *
     * @param target Bytes to convert
     * @return Stream Stream to use
     */
    public static Stream<Byte> of(final byte... target) {

        final var buffer = ByteBuffer.wrap(target);
        return Stream.generate(buffer::get).limit(buffer.capacity());
    }

    /**
     * Converts an Enumeration to a stream.
     *
     * @param target Enumeration to convert
     * @param <T>    Parameterized type
     * @return Stream Stream to use
     */
    public static <T> Stream<T> from(final Enumeration<T> target) {

        return from(Iterators.from(target));
    }

    /**
     * Converts an Iterator to a non-parallized Stream.
     *
     * @param target Iterator to stream
     * @param <T>    Parameterized type
     * @return Stream Stream version of Iterable
     */
    public static <T> Stream<T> from(final Iterator<T> target) {

        return from(Iterables.from(target));
    }

    /**
     * Converts an Iterable to a non-parallized Stream.
     *
     * @param target Iterable to stream
     * @param <T>    Parameterized type
     * @return Stream Stream version of Iterable
     */
    public static <T> Stream<T> from(final Iterable<T> target) {

        return StreamSupport.stream(target.spliterator(), false);
    }
}
