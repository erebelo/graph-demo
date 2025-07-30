/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.api;

import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Path;

import java.util.List;

/**
 * Service for performing graph-wide operations like path finding and connectivity analysis.
 */
public interface GraphService {

    /**
     * Checks if a path exists between two nodes in the active graph.
     */
    boolean hasPath(NanoId sourceNodeId, NanoId targetNodeId);

    /**
     * Returns all currently connected paths in the active graph.
     */
    List<Path> getActiveConnected();

    /**
     * Finds the shortest path between two nodes in the active graph.
     */
    Path getShortestPath(NanoId sourceNodeId, NanoId targetNodeId);
}
