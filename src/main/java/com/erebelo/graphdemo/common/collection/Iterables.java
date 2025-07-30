/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Contains utilities to facilitate using Iterable instances. These methods will always return an immutable version of an iterable. For items that
 * normally mutate one in place (like a sort operation), an immutable copy will be returned.
 */
public final class Iterables {

    /**
     * Type contains only static members.
     */
    private Iterables() {
    }

    /**
     * Ensures the collection passed in will be returned as an immutable iterable.
     *
     * @param target Collection
     * @param <T>    Parameterized type
     * @return Iterable Immutable iterable
     */
    public static <T> Iterable<T> immutable(final Collection<T> target) {

        return List.copyOf(target);
    }

    /**
     * Returns an immutable iterable containing the specified values.
     *
     * @param values Values to store
     * @param <T>    Parameterized type
     * @return Iterable Immutable iterable
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Iterable<T> of(final T... values) {

        return List.of(values.clone());
    }

    /**
     * Sorts the specified Iterable with the comparator supplied. Note that this is not an in-place sort. Rather, the Iterable is a new instance.
     *
     * @param target  Target to sort
     * @param compare Comparator to use
     * @param <T>     Parameterized type of the iterable
     * @return Iterable Sorted, immutable iterable (new copy)
     */
    public static <T> Iterable<T> sort(final Iterable<T> target, final Comparator<T> compare) {

        return immutable(Streams.from(target).sorted(compare).collect(Collectors.toList()));
    }

    /**
     * Converts an Enumeration to an Iterable (normally used for a foreach loop).
     *
     * @param target Enumeration to convert
     * @param <T>    Parameterized type of iterable
     * @return Iterable Iterable to use (note can only be consumed once)
     */
    public static <T> Iterable<T> from(final Enumeration<T> target) {

        return from(Iterators.from(target));
    }

    /**
     * Converts an Iterator to an Iterable (normally used for a foreach loop).
     *
     * @param iterator Iterator to convert
     * @param <T>      Parameterized type of iterable
     * @return Iterable Iterable to use (note can only be consumed once)
     */
    public static <T> Iterable<T> from(final Iterator<T> iterator) {

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .collect(Collectors.toList());
    }
}
