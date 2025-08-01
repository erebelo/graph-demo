/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */
package com.erebelo.graphdemo.persistence;

import com.erebelo.graphdemo.common.annotation.Stable;
import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.fp.Proc0;
import com.erebelo.graphdemo.common.version.Locateable;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Graph listener that queues up database operations in response to graph events and then executes then defers execution until the flush() method is
 * called.
 */
@Repository("delegatedGraphListenerRepository")
@Stable
public class DelegatedGraphListenerRepository implements GraphListenerRepository {

    private final Collection<Proc0> operations = new ArrayList<>();

    private final GraphRepository delegate;

    public DelegatedGraphListenerRepository(final GraphRepository delegate) {

        this.delegate = delegate;
    }

    @Override
    public void vertexAdded(final GraphVertexChangeEvent<Reference<Node>> event) {
        Optional<Node> nodeOpt = unwrapReference(event.getVertex());
        nodeOpt.ifPresent(node -> operations.add(() -> delegate.nodes().save(node)));
    }

    @Override
    public void vertexRemoved(final GraphVertexChangeEvent<Reference<Node>> event) {
        Optional<Node> nodeOpt = unwrapReference(event.getVertex());
        nodeOpt.ifPresent(node -> {
            Instant expiredTime = node.expired().orElseGet(Instant::now);
            operations.add(() -> delegate.nodes().expire(node.locator().id(), expiredTime));
        });
    }

    @Override
    public void edgeAdded(final GraphEdgeChangeEvent<Reference<Node>, Reference<Edge>> event) {
        Optional<Edge> edgeOpt = unwrapReference(event.getEdge());
        edgeOpt.ifPresent(edge -> operations.add(() -> delegate.edges().save(edge)));
    }

    @Override
    public void edgeRemoved(final GraphEdgeChangeEvent<Reference<Node>, Reference<Edge>> event) {
        Optional<Edge> edgeOpt = unwrapReference(event.getEdge());
        edgeOpt.ifPresent(edge -> {
            Instant expiredTime = edge.expired().orElseGet(Instant::now);
            operations.add(() -> delegate.edges().expire(edge.locator().id(), expiredTime));
        });
    }

    @Override
    public void flush() {
        Io.withVoid(() -> {
            operations.forEach(proc -> Io.withVoid(proc));
            operations.clear();
        });
    }

    /**
     * Utility to safely unwrap a Reference<T> to T if loaded.
     */
    private static <T extends Locateable> Optional<T> unwrapReference(Reference<T> reference) {
        if (reference instanceof Reference.Loaded<T>(T value)) {
            return Optional.of(value);
        } else {
            // Unloaded references are ignored here.
            // You could also log a warning or throw if you prefer.
            return Optional.empty();
        }
    }
}
