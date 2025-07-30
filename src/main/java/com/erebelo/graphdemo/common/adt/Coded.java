/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

/**
 * Represents a type that has a code value (such as to identify it in a database or lookup). Used primarily with CodedFinder.
 *
 * @param <T> Parameterized type of the code value
 */
@FunctionalInterface
public interface Coded<T> {

    /**
     * Returns the code value associated with the type.
     *
     * @return T Code value
     */
    T code();
}
