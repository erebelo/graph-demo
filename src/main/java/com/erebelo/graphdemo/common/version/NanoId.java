/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.version;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.erebelo.graphdemo.common.annotation.Stable;

/**
 * Encapsulates a Nano ID used to uniquely identify an item, like a UUID, but compliant for REST or microservice usage. This record is immutable and
 * thread-safe.
 */
@Stable
public record NanoId(String id) {

    /**
     * Generate a new, random Nano ID.
     */
    public static NanoId generate() {

        final var generated = NanoIdUtils.randomNanoId();
        return new NanoId(generated);
    }
}
