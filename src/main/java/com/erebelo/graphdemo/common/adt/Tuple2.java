/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.adt;

/**
 * Simple tuple holding two values.
 *
 * @param _1
 *            First value
 * @param _2
 *            Second value
 * @param <L>
 *            Type of first value
 * @param <R>
 *            Type of second value
 */
public record Tuple2<L, R>(L _1, R _2) {
}
