/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

import com.erebelo.graphdemo.common.collection.Streams;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Searches various data structures which implement the Coded interface. This finder is useful when retrieving a value from, say, a database and
 * wanting to map that to a specific enum constant, collection item or array item.
 */
public final class CodedFinder {

    /**
     * Type contains only static members.
     */
    private CodedFinder() {
    }

    /**
     * Locates the coded value in an enum that implements the Coded interface.
     *
     * @param code      Code value to match
     * @param enumClass Enum class to search
     * @param <C>       Parameterized type of the code
     * @param <T>       Parameterized type of the enum
     * @return Optional Resuting enum constant if found
     */
    public static <C, T extends Enum<T> & Coded<C>> Optional<T> find(final C code, final Class<T> enumClass) {

        return find(code, enumClass.getEnumConstants());
    }

    /**
     * Locates the coded value in an array whose contents implement the Coded interface.
     *
     * @param <C>    Parameterized type of the code
     * @param <T>    Parameterized type of the array
     * @param code   Code value to match
     * @param values Array to search
     * @return Optional Resulting value if found
     */
    public static <C, T extends Coded<C>> Optional<T> find(final C code, final T[] values) {

        return find(code, Arrays.asList(values));
    }

    /**
     * Locates the coded value in an iterable whose contents implement the Coded interface.
     *
     * @param <C>    Parameterized type of the code
     * @param <T>    Parameterized type of the iterable
     * @param code   Code value to match
     * @param values Iterable to search
     * @return Optional Resulting value if found
     */
    public static <C, T extends Coded<C>> Optional<T> find(final C code, final Iterable<T> values) {

        return Streams.from(values).filter(t -> t.code().equals(code)).findFirst();
    }

    /**
     * Locates the coded value in an enum that implements the Coded interface.
     *
     * @param code      Code value to match
     * @param enumClass Enum class to search
     * @param <C>       Parameterized type of the code
     * @param <T>       Parameterized type of the enum
     * @return Optional Resuting enum constant if found
     */
    public static <C, T extends Enum<T> & Coded<C>> T require(final C code, final Class<T> enumClass) {

        return find(code, enumClass).orElseThrow(toException(code));
    }

    /**
     * Locates the coded value in an array whose contents implement the Coded interface.
     *
     * @param <C>    Parameterized type of the code
     * @param <T>    Parameterized type of the array
     * @param code   Code value to match
     * @param values Array to search
     * @return Optional Resulting value if found
     */
    public static <C, T extends Coded<C>> T require(final C code, final T[] values) {

        return find(code, values).orElseThrow(toException(code));
    }

    /**
     * Locates the coded value in an iterable whose contents implement the Coded interface.
     *
     * @param <C>    Parameterized type of the code
     * @param <T>    Parameterized type of the iterable
     * @param code   Code value to match
     * @param values Iterable to search
     * @return Optional Resulting value if found
     */
    public static <C, T extends Coded<C>> T require(final C code, final Iterable<T> values) {

        return find(code, values).orElseThrow(toException(code));
    }

    /**
     * Helper method to populate a consistent error message.
     */
    private static <C> Supplier<RuntimeException> toException(final C code) {

        return () -> new IllegalArgumentException("Unknown value '%s' received".formatted(code));
    }
}
