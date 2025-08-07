/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.api.impl;

import com.erebelo.graphdemo.api.GraphService;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Path;
import com.erebelo.graphdemo.model.jgrapht.GraphOperations;
import com.erebelo.graphdemo.model.jgrapht.NodeOperations;
import com.erebelo.graphdemo.persistence.GraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of GraphService using session-based transactions.
 */
@Service
public final class DefaultGraphService implements GraphService {

    private final GraphRepository repository;
    private final GraphOperations graphOperations;
    private final NodeOperations nodeOperations;
    // FIXME: PathOperations should be injected once SimpleMutableGraph is implemented
    // private final PathOperations pathOperations;

    public DefaultGraphService(
            final GraphRepository repository,
            final GraphOperations graphOperations,
            final NodeOperations nodeOperations) {

        this.repository = repository;
        this.graphOperations = graphOperations;
        this.nodeOperations = nodeOperations;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPath(final NanoId sourceNodeId, final NanoId targetNodeId) {

        final var sourceNode = nodeOperations
                .findActive(sourceNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Source node not found: " + sourceNodeId));
        final var targetNode = nodeOperations
                .findActive(targetNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Target node not found: " + targetNodeId));

        // FIXME: Use pathOperations once it's properly injected
        throw new UnsupportedOperationException("PathOperations not yet integrated");
        // return pathOperations.pathExists(sourceNode, targetNode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Path> getActiveConnected() {

        // Get all active nodes
        final var activeNodes = nodeOperations.allActive();

        // Find all connected paths among active nodes
        final var connectedPaths = new java.util.ArrayList<Path>();

        for (var i = 0; i < activeNodes.size(); i++) {
            for (var j = i + 1; j < activeNodes.size(); j++) {
                final var sourceNode = activeNodes.get(i);
                final var targetNode = activeNodes.get(j);

                // FIXME: Use pathOperations once it's properly injected
                // if (pathOperations.pathExists(sourceNode, targetNode)) {
                //     final var paths = pathOperations.allPaths(sourceNode, targetNode);
                if (false) {
                    final var paths = List.<Path>of();
                    connectedPaths.addAll(paths);
                }
            }
        }

        return connectedPaths;
    }

    @Override
    @Transactional(readOnly = true)
    public Path getShortestPath(final NanoId sourceNodeId, final NanoId targetNodeId) {

        final var sourceNode = nodeOperations
                .findActive(sourceNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Source node not found: " + sourceNodeId));
        final var targetNode = nodeOperations
                .findActive(targetNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Target node not found: " + targetNodeId));

        // FIXME: Use pathOperations once it's properly injected
        throw new UnsupportedOperationException("PathOperations not yet integrated");
        // return pathOperations.shortestPath(sourceNode, targetNode);
    }
}
