/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.tinkerpop;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Element;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.serde.PropertiesSerde;
import com.erebelo.graphdemo.model.serde.Serde;
import com.erebelo.graphdemo.model.simple.SimpleComponent;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tinkerpop implementation of ComponentRepository.
 */
@Repository("tinkerpopComponentRepository")
public final class TinkerpopComponentRepository implements ExtendedVersionedRepository<Component> {

    private final Graph graph;
    private final GraphTraversalSource traversal;
    private final TinkerpopNodeRepository nodeRepository;
    private final TinkerpopEdgeRepository edgeRepository;
    private final Serde<Map<String, Object>> serde = new PropertiesSerde();

    public TinkerpopComponentRepository(
            final Graph graph,
            final TinkerpopNodeRepository nodeRepository,
            final TinkerpopEdgeRepository edgeRepository) {
        this.graph = graph;
        traversal = graph.traversal();
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
    public Component save(final Component component) {
        return Io.withReturn(() -> {
            final var vertex = graph.addVertex("component");

            vertex.property("id", component.locator().id().id());
            vertex.property("version", component.locator().version());
            vertex.property("created", component.created().toString());
            component.expired().ifPresent(exp -> vertex.property("expired", exp.toString()));

            // Store data properties directly on the vertex
            final var properties = serde.serialize(component.data());
            properties.forEach(vertex::property);

            // Save element associations
            for (final var element : component.elements()) {
                final var elementVertex = traversal
                        .V()
                        .has("id", element.locator().id().id())
                        .has("version", element.locator().version())
                        .tryNext();

                elementVertex.ifPresent(value -> vertex.addEdge("contains", value)
                        .property("elementType", element.getClass().getSimpleName()));
            }

            return component;
        });
    }

    @Override
    public Optional<Component> findActive(final NanoId id) {
        return Io.withReturn(() -> traversal
                .V()
                .hasLabel("component")
                .has("id", id.id())
                .not(__.has("expired"))
                .order()
                .by("version", Order.desc)
                .limit(1)
                .tryNext()
                .map(this::vertexToComponent));
    }

    @Override
    public Optional<Component> findAt(final NanoId id, final Instant timestamp) {
        return Io.withReturn(() -> {
            final var timestampStr = timestamp.toString();
            return traversal
                    .V()
                    .hasLabel("component")
                    .has("id", id.id())
                    .where(__.values("created").is(P.lte(timestampStr)))
                    .where(__.or(__.not(__.has("expired")), __.values("expired").is(P.gt(timestampStr))))
                    .order()
                    .by("version", Order.desc)
                    .limit(1)
                    .tryNext()
                    .map(this::vertexToComponent);
        });
    }

    @Override
    public Optional<Component> find(final Locator locator) {
        return Io.withReturn(() -> traversal
                .V()
                .hasLabel("component")
                .has("id", locator.id().id())
                .has("version", locator.version())
                .tryNext()
                .map(this::vertexToComponent));
    }

    @Override
    public List<Component> findAll(final NanoId id) {
        return Io.withReturn(
                () -> traversal.V().hasLabel("component").has("id", id.id()).order().by("version").toList().stream()
                        .map(this::vertexToComponent)
                        .toList());
    }

    @Override
    public boolean expire(final NanoId id, final Instant expiredAt) {
        return Io.withReturn(() -> {
            final var vertices = traversal
                    .V()
                    .hasLabel("component")
                    .has("id", id.id())
                    .not(__.has("expired"))
                    .toList();
            if (!vertices.isEmpty()) {
                vertices.forEach(v -> v.property("expired", expiredAt.toString()));
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean delete(final NanoId id) {
        return Io.withReturn(() -> {
            final var count = traversal
                    .V()
                    .hasLabel("component")
                    .has("id", id.id())
                    .count()
                    .next();
            if (count > 0) {
                // First delete all edges
                traversal
                        .V()
                        .hasLabel("component")
                        .has("id", id.id())
                        .bothE()
                        .drop()
                        .iterate();

                // Then delete the vertices
                traversal.V().hasLabel("component").has("id", id.id()).drop().iterate();
                return true;
            }
            return false;
        });
    }

    @Override
    public List<NanoId> allIds() {
        return Io.withReturn(() -> traversal.V().hasLabel("component").values("id").dedup().toList().stream()
                .map(id -> new NanoId((String) id))
                .toList());
    }

    @Override
    public List<NanoId> allActiveIds() {
        return Io.withReturn(
                () -> traversal.V().hasLabel("component").not(__.has("expired")).values("id").dedup().toList().stream()
                        .map(id -> new NanoId((String) id))
                        .toList());
    }

    private Component vertexToComponent(final Vertex vertex) {
        final var id = new NanoId(vertex.value("id"));
        final var version = vertex.<Integer>value("version");
        final var locator = new Locator(id, version);
        final var created = Instant.parse(vertex.value("created"));

        final var expired = vertex.property("expired").isPresent()
                ? Optional.of(Instant.parse(vertex.value("expired")))
                : Optional.<Instant>empty();

        // Reconstruct data from vertex properties
        final var properties = vertex.keys().stream()
                .filter(key -> !List.of("id", "version", "created", "expired").contains(key))
                .collect(Collectors.toMap(Function.identity(), vertex::<Object>value));
        final var data = serde.deserialize(properties);

        // Load elements associated with this component
        final var elements = loadComponentElements(vertex);

        return new SimpleComponent(locator, elements, data, created, expired);
    }

    private List<Reference<Element>> loadComponentElements(final Vertex componentVertex) {
        final var elements = new ArrayList<Reference<Element>>();

        traversal
                .V(componentVertex)
                .outE("contains")
                .as("edge")
                .inV()
                .as("element")
                .select("edge", "element")
                .toList()
                .forEach(map -> {
                    final var elementVertex = (Vertex) map.get("element");
                    final var edge = (Edge) map.get("edge");
                    final var elementType = edge.value("elementType");

                    if ("SimpleNode".equals(elementType)) {
                        nodeRepository
                                .find(new Locator(
                                        new NanoId(elementVertex.value("id")), elementVertex.<Integer>value("version")))
                                .ifPresent(node -> elements.add(new Reference.Loaded<>(node)));
                    } else if ("SimpleEdge".equals(elementType)) {
                        edgeRepository
                                .find(new Locator(
                                        new NanoId(elementVertex.value("id")), elementVertex.<Integer>value("version")))
                                .ifPresent(edgeElement -> elements.add(new Reference.Loaded<>(edgeElement)));
                    }
                });

        return elements;
    }
}
