/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.api;

import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;

import java.util.List;

/**
 * Service for retrieving and manipulating edges in the graph.  Within the graph, two nodes can contain multiple, parallel edges.
 */
public interface EdgeService extends IdentifiableBase<Edge> {

    /**
     * Creates a new edge between the specified nodes and with the supplied data.
     */
    Edge addEdge(Node source, Node target, Data data);

    /**
     * Updates the specified edget with new data, creating a new version.
     */
    Edge updateEdge(NanoId id, Data data);

    /**
     * Returns the edges from the specified node.
     */
    List<Edge> getEdgesFrom(NanoId nodeId);

    /**
     * Returns the edges to the specified node.
     */
    List<Edge> getEdgesTo(NanoId nodeId);
}
