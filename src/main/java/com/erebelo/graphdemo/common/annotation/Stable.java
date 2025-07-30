/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks any element type in a source file as stable. This is used for guidance to developers and coding agents to prompt before modifying the
 * annotated element type. This annotation is immutable and thread-safe.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Stable {
}
