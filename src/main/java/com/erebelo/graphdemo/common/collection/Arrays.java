/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.collection;

import java.lang.reflect.Array;

/**
 * Helper methods to facilitate working with arrays.
 */
public final class Arrays {

    /**
     * Type contains only static members.
     */
    private Arrays() {
    }

    /**
     * Convert a class to the array class equivalent.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T[]> toArray(final Class<T> target) {

        return (Class<T[]>) Array.newInstance(target, 0).getClass();
    }
}
