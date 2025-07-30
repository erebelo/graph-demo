/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model;

/**
 * Container for typed data that can be stored with graph elements.
 */
public interface Data {

    /**
     * Returns the class type of the contained value.
     */
    Class<?> javaClass();

    /**
     * Returns the actual data value.
     */
    Object value();
}
