/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.tinkerpop;

import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.serde.PropertiesSerde;
import com.erebelo.graphdemo.model.serde.Serde;
import com.erebelo.graphdemo.model.simple.SimpleEdge;
import com.erebelo.graphdemo.model.simple.SimpleType;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.tinkerpop.gremlin.process.traversal.P.gt;
import static org.apache.tinkerpop.gremlin.process.traversal.P.lte;

/**
 * Tinkerpop implementation of EdgeRepository.
 */
@Repository("tinkerpopEdgeRepository")
public final class TinkerpopEdgeRepository implements ExtendedVersionedRepository<Edge> {

    private final GraphTraversalSource traversal;
    private final TinkerpopNodeRepository nodeRepository;
    private final Serde<Map<String, Object>> serde = new PropertiesSerde();

    public TinkerpopEdgeRepository(final @NotNull Graph graph, final TinkerpopNodeRepository nodeRepository) {
        traversal = graph.traversal();
        this.nodeRepository = nodeRepository;
    }

    @Override
    public Edge save(final @NotNull Edge edge) {
        // Extract nodes from references
        final Node sourceNode;
        final Node targetNode;
        if (edge.source() instanceof Reference.Loaded<Node> loadedSource) {
            sourceNode = loadedSource.value();
        } else {
            throw new IllegalArgumentException("Source node must be loaded to save edge");
        }
        if (edge.target() instanceof Reference.Loaded<Node> loadedTarget) {
            targetNode = loadedTarget.value();
        } else {
            throw new IllegalArgumentException("Target node must be loaded to save edge");
        }

        final var sourceVertex = findOrCreateVertexForNode(sourceNode);
        final var targetVertex = findOrCreateVertexForNode(targetNode);

        final var tinkerpopEdge = sourceVertex.addEdge("edge", targetVertex);
        tinkerpopEdge.property("id", edge.locator().id().id());
        tinkerpopEdge.property("versionId", edge.locator().version());
        tinkerpopEdge.property("type", edge.type().code());
        tinkerpopEdge.property("sourceId", edge.source().locator().id().id());
        tinkerpopEdge.property("sourceVersionId", edge.source().locator().version());
        tinkerpopEdge.property("targetId", edge.target().locator().id().id());
        tinkerpopEdge.property("targetVersionId", edge.target().locator().version());
        tinkerpopEdge.property("created", edge.created().toString());
        edge.expired().ifPresent(expired -> tinkerpopEdge.property("expired", expired.toString()));

        final var properties = serde.serialize(edge.data());
        properties.forEach(tinkerpopEdge::property);

        return edge;
    }

    @Override
    public Optional<Edge> findActive(final NanoId edgeId) {
        final var edgeOpt =
                traversal.E().has("id", edgeId.id()).not(__.has("expired")).tryNext();
        return edgeOpt.map(this::edgeToEdge);
    }

    @Override
    public List<Edge> findAll(final NanoId edgeId) {
        return traversal.E().has("id", edgeId.id()).order().by("versionId").toList().stream()
                .map(this::edgeToEdge)
                .toList();
    }

    @Override
    public Optional<Edge> find(final Locator locator) {
        final var edgeOpt = traversal
                .E()
                .has("id", locator.id().id())
                .has("versionId", locator.version())
                .tryNext();
        return edgeOpt.map(this::edgeToEdge);
    }

    @Override
    public Optional<Edge> findAt(final NanoId edgeId, final Instant timestamp) {
        final var timestampStr = timestamp.toString();
        return traversal
                .E()
                .has("id", edgeId.id())
                .where(__.values("created").is(lte(timestampStr)))
                .where(__.or(__.not(__.has("expired")), __.values("expired").is(gt(timestampStr))))
                .order()
                .by("versionId", Order.desc)
                .limit(1)
                .tryNext()
                .map(this::edgeToEdge);
    }

    @Override
    public boolean delete(final NanoId edgeId) {
        final var count = traversal.E().has("id", edgeId.id()).count().next();
        if (count > 0) {
            traversal.E().has("id", edgeId.id()).drop().iterate();
            return true;
        }
        return false;
    }

    @Override
    public boolean expire(final NanoId elementId, final Instant expiredAt) {
        final var edges =
                traversal.E().has("id", elementId.id()).not(__.has("expired")).toList();
        if (!edges.isEmpty()) {
            edges.forEach(e -> e.property("expired", expiredAt.toString()));
            return true;
        }
        return false;
    }

    @Override
    public List<NanoId> allIds() {
        return traversal.E().values("id").dedup().toList().stream()
                .map(id -> new NanoId((String) id))
                .toList();
    }

    @Override
    public List<NanoId> allActiveIds() {
        return traversal.E().not(__.has("expired")).values("id").dedup().toList().stream()
                .map(id -> new NanoId((String) id))
                .toList();
    }

    private Vertex findOrCreateVertexForNode(final Node node) {
        final var existing = traversal.V().has("id", node.locator().id().id()).tryNext();
        if (existing.isPresent()) {
            return existing.get();
        }
        // Create vertex if it doesn't exist
        nodeRepository.save(node);
        return traversal.V().has("id", node.locator().id().id()).next();
    }

    private Edge edgeToEdge(final org.apache.tinkerpop.gremlin.structure.Edge tinkerpopEdge) {
        final var id = new NanoId(tinkerpopEdge.value("id"));
        final int versionId = tinkerpopEdge.value("versionId");
        final var type = new SimpleType(tinkerpopEdge.value("type"));
        final var created = Instant.parse(tinkerpopEdge.value("created"));

        Optional<Instant> expired = Optional.empty();
        if (tinkerpopEdge.property("expired").isPresent()) {
            expired = Optional.of(Instant.parse(tinkerpopEdge.value("expired")));
        }

        final var properties = tinkerpopEdge.keys().stream()
                .filter(key -> !List.of(
                                "id",
                                "versionId",
                                "type",
                                "sourceId",
                                "sourceVersionId",
                                "targetId",
                                "targetVersionId",
                                "created",
                                "expired")
                        .contains(key))
                .collect(Collectors.toMap(Function.identity(), tinkerpopEdge::<Object>value));

        final var data = serde.deserialize(properties);
        final var locator = new Locator(id, versionId);

        // Get source and target nodes using the stored version information
        final var sourceId = new NanoId(tinkerpopEdge.value("sourceId"));
        final var sourceVersionId = (Integer) tinkerpopEdge.value("sourceVersionId");
        final var targetId = new NanoId(tinkerpopEdge.value("targetId"));
        final var targetVersionId = (Integer) tinkerpopEdge.value("targetVersionId");

        final var sourceNode =
                nodeRepository.find(new Locator(sourceId, sourceVersionId)).orElseThrow();
        final var targetNode =
                nodeRepository.find(new Locator(targetId, targetVersionId)).orElseThrow();

        // Find components containing this edge
        final var componentRefs = new HashSet<Reference<Component>>();
        traversal
                .E()
                .hasLabel("component-element")
                .has("elementId", id.id())
                .has("elementVersionId", versionId)
                .has("elementType", "SimpleEdge")
                .toList()
                .forEach(componentEdge -> {
                    final var componentId = new NanoId(componentEdge.value("componentId"));
                    final var componentVersion = (int) componentEdge.value("componentVersionId");
                    final var componentLocator = new Locator(componentId, componentVersion);
                    componentRefs.add(new Reference.Unloaded<>(componentLocator, Component.class));
                });

        return new SimpleEdge(
                locator,
                type,
                new Reference.Loaded<>(sourceNode),
                new Reference.Loaded<>(targetNode),
                data,
                created,
                expired,
                componentRefs);
    }
}
