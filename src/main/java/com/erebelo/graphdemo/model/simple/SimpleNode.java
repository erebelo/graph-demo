/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.simple;

import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.Type;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record SimpleNode(
        Locator locator,
        Type type,
        List<Reference<Edge>> edges,
        Data data,
        Instant created,
        Optional<Instant> expired,
        Set<Reference<Component>> components)
        implements Node {

    /**
     * Constructor that initializes components to empty set and type to SimpleType("node").
     */
    public SimpleNode(
            final Locator locator,
            final List<Reference<Edge>> edges,
            final Data data,
            final Instant created,
            final Optional<Instant> expired) {

        this(locator, new SimpleType("node"), edges, data, created, expired, new HashSet<>());
    }
}
