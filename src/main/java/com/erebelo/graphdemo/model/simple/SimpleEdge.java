/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.simple;

import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Type;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public record SimpleEdge(Locator locator, Type type, Node source, Node target, Data data, Instant created,
        Optional<Instant> expired, Set<NanoId> components) implements Edge {

    /**
     * Constructor that initializes components to empty set and type to
     * SimpleType("edge").
     */
    public SimpleEdge(final Locator locator, final Node source, final Node target, final Data data,
            final Instant created, final Optional<Instant> expired) {

        this(locator, new SimpleType("edge"), source, target, data, created, expired, new HashSet<>());
    }
}
