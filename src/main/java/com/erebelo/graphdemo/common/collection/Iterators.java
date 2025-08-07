/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Convenience methods for working with iterators.
 */
public final class Iterators {

    /**
     * Type contains only static members.
     */
    private Iterators() {
    }

    /**
     * Converts an Enumeration to an iterator.
     *
     * @param target
     *            Enumeration to convert
     * @param <T>
     *            Parameteried type
     * @return Iterator Iterator to use
     */
    public static <T> Iterator<T> from(final Enumeration<T> target) {

        return new Iterator<>() {

            @Override
            public boolean hasNext() {

                return target.hasMoreElements();
            }

            @Override
            public T next() {

                return target.nextElement();
            }
        };
    }

    /**
     * Safely removes elements from an iterator based on the test passed in.
     *
     * @param target
     *            Iterator to inspect
     * @param test
     *            Test to apply
     * @param <T>
     *            Parameterized type
     * @return int Number of elements removed
     */
    @SuppressWarnings("ReassignedVariable")
    public static <T> int remove(final Iterator<T> target, final Predicate<T> test) {

        var count = 0;
        while (target.hasNext()) {
            final var existing = target.next();
            if (test.test(existing)) {
                target.remove();
                count++;
            }
        }
        return count;
    }
}
