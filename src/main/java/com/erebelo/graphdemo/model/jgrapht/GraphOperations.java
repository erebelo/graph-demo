/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.jgrapht;

import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.springframework.stereotype.Service;

/**
 * Operations that apply to the entire graph.
 */
@Service
public class GraphOperations {

    /**
     * JGraphT delegated in memory graph.
     */
    private final Graph<Node, Edge> graph;

    /**
     * Creates graph operations that will forward any events on to the specified
     * listener.
     */
    public GraphOperations(final GraphListener<Node, Edge> listener) {

        final Graph<Node, Edge> base = new DirectedMultigraph<>(null, null, false);
        final ListenableGraph<Node, Edge> wrapper = new DefaultListenableGraph<>(base);
        wrapper.addGraphListener(listener);
        graph = wrapper;
    }
}
