package com.erebelo.graphdemo.model.jgrapht;

import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Path;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Operations that derive or compute paths between nodes.
 */
@Service
public class PathOperations {

    /**
     * Delegate graph.
     */
    private final Graph<Node, Edge> graph;

    /**
     * Creates a new path operations delegating to the underlying graph.
     */
    public PathOperations(final Graph<Node, Edge> graph) {

        this.graph = graph;
    }

    /**
     * Finds shortest path between two nodes.
     */
    public Path shortestPath(final Node source, final Node target) {

        final var pathAlgorithm = new DijkstraShortestPath<>(graph);
        final var jgraphtPath = pathAlgorithm.getPath(source, target);
        if (jgraphtPath == null) {
            return new Path(List.of());
        }
        return OperationsHelper.toPath(jgraphtPath);
    }

    /**
     * Checks if path exists between two nodes.
     */
    public boolean pathExists(final Node source, final Node target) {

        final var inspector = new ConnectivityInspector<>(graph);
        return inspector.pathExists(source, target);
    }

    /**
     * Returns all possible paths between two nodes.
     */
    public List<Path> allPaths(final Node source, final Node target) {

        final var allPathsAlgorithm = new AllDirectedPaths<>(graph);
        final var maxPathLength = graph.vertexSet().size();
        final var jgraphtPaths = allPathsAlgorithm.getAllPaths(source, target, true, maxPathLength);
        return jgraphtPaths.stream()
                .map(OperationsHelper::toPath)
                .filter(path -> !OperationsHelper.containsCycle(path))
                .toList();
    }
}
