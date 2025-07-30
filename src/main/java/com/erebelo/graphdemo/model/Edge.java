/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package com.erebelo.graphdemo.model;

import com.erebelo.graphdemo.common.annotation.Stable;

/**
 * Represents a directed edge in the graph connecting two nodes. Implementations must be immutable and thread-safe.
 */
@Stable
public interface Edge extends Element {

    /**
     * Returns the source node of this edge.
     */
    Reference<Node> source();

    /**
     * Returns the target node of this edge.
     */
    Reference<Node> target();
}
