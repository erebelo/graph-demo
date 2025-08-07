/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import com.erebelo.graphdemo.persistence.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB implementation of GraphRepository compatible with DelegatedGraphListenerRepository.
 */
@Repository("mongoGraphRepository")
public class MongoGraphRepository implements GraphRepository {

    private final MongoNodeRepository nodes;
    private final MongoEdgeRepository edges;
    private final MongoComponentRepository components;
    private final MongoGraphOperations graphOperations;

    public MongoGraphRepository(MongoNodeRepository nodes, MongoEdgeRepository edges, MongoComponentRepository components,
            MongoGraphOperations graphOperations) {
        this.nodes = nodes;
        this.edges = edges;
        this.components = components;
        this.graphOperations = graphOperations;
    }

    public static MongoGraphRepository create(final MongoSession session) {

        final var nodeRepository = new MongoNodeRepository(session.database());
        final var edgeRepository = new MongoEdgeRepository(session.database(), nodeRepository);
        final var graphOperations = new MongoGraphOperations(session.database(), nodeRepository, edgeRepository);
        return new MongoGraphRepository(
                nodeRepository,
                edgeRepository,
                new MongoComponentRepository(session.database(), nodeRepository, edgeRepository),
                graphOperations);
    }

    @Override
    public ExtendedVersionedRepository<Node> nodes() {
        return nodes;
    }

    @Override
    public ExtendedVersionedRepository<Edge> edges() {
        return edges;
    }

    @Override
    public ExtendedVersionedRepository<Component> components() {
        return components;
    }
}
