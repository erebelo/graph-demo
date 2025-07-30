/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.simple;

import com.erebelo.graphdemo.model.Data;

/**
 * Simple implementation of Data interface.
 */
public record SimpleData(Class<?> javaClass, Object value) implements Data {
}
