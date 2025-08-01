package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Configuration
public class GraphConfig {

    @Bean
    public Graph<Reference<Node>, Reference<Edge>> graph() {
        return new Graph<Reference<Node>, Reference<Edge>>() {
            @Override
            public Set<Reference<Edge>> getAllEdges(Reference<Node> nodeReference, Reference<Node> v1) {
                return Set.of();
            }

            @Override
            public Reference<Edge> getEdge(Reference<Node> nodeReference, Reference<Node> v1) {
                return null;
            }

            @Override
            public Supplier<Reference<Node>> getVertexSupplier() {
                return null;
            }

            @Override
            public Supplier<Reference<Edge>> getEdgeSupplier() {
                return null;
            }

            @Override
            public Reference<Edge> addEdge(Reference<Node> nodeReference, Reference<Node> v1) {
                return null;
            }

            @Override
            public boolean addEdge(Reference<Node> nodeReference, Reference<Node> v1, Reference<Edge> edgeReference) {
                return false;
            }

            @Override
            public Reference<Node> addVertex() {
                return null;
            }

            @Override
            public boolean addVertex(Reference<Node> nodeReference) {
                return false;
            }

            @Override
            public boolean containsEdge(Reference<Node> nodeReference, Reference<Node> v1) {
                return false;
            }

            @Override
            public boolean containsEdge(Reference<Edge> edgeReference) {
                return false;
            }

            @Override
            public boolean containsVertex(Reference<Node> nodeReference) {
                return false;
            }

            @Override
            public Set<Reference<Edge>> edgeSet() {
                return Set.of();
            }

            @Override
            public int degreeOf(Reference<Node> nodeReference) {
                return 0;
            }

            @Override
            public Set<Reference<Edge>> edgesOf(Reference<Node> nodeReference) {
                return Set.of();
            }

            @Override
            public int inDegreeOf(Reference<Node> nodeReference) {
                return 0;
            }

            @Override
            public Set<Reference<Edge>> incomingEdgesOf(Reference<Node> nodeReference) {
                return Set.of();
            }

            @Override
            public int outDegreeOf(Reference<Node> nodeReference) {
                return 0;
            }

            @Override
            public Set<Reference<Edge>> outgoingEdgesOf(Reference<Node> nodeReference) {
                return Set.of();
            }

            @Override
            public boolean removeAllEdges(Collection<? extends Reference<Edge>> collection) {
                return false;
            }

            @Override
            public Set<Reference<Edge>> removeAllEdges(Reference<Node> nodeReference, Reference<Node> v1) {
                return Set.of();
            }

            @Override
            public boolean removeAllVertices(Collection<? extends Reference<Node>> collection) {
                return false;
            }

            @Override
            public Reference<Edge> removeEdge(Reference<Node> nodeReference, Reference<Node> v1) {
                return null;
            }

            @Override
            public boolean removeEdge(Reference<Edge> edgeReference) {
                return false;
            }

            @Override
            public boolean removeVertex(Reference<Node> nodeReference) {
                return false;
            }

            @Override
            public Set<Reference<Node>> vertexSet() {
                return Set.of();
            }

            @Override
            public Reference<Node> getEdgeSource(Reference<Edge> edgeReference) {
                return null;
            }

            @Override
            public Reference<Node> getEdgeTarget(Reference<Edge> edgeReference) {
                return null;
            }

            @Override
            public GraphType getType() {
                return null;
            }

            @Override
            public double getEdgeWeight(Reference<Edge> edgeReference) {
                return 0;
            }

            @Override
            public void setEdgeWeight(Reference<Edge> edgeReference, double v) {

            }
        };
    }
}
