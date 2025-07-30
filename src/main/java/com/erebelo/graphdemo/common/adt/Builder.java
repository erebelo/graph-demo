/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

/**
 * Internal interface for returning a builder. This interface is immutable and thread-safe when implementations are properly implemented.
 */
@FunctionalInterface
public interface Builder<T> {

    /**
     * Causes the builder to return a fully built instance of T.
     */
    T build();
}
