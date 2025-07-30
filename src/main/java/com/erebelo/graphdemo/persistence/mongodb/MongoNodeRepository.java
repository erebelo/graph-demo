/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.serde.JsonSerde;
import com.erebelo.graphdemo.model.serde.Serde;
import com.erebelo.graphdemo.model.simple.SimpleNode;
import com.erebelo.graphdemo.model.simple.SimpleType;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

/**
 * MongoDB implementation of NodeRepository using JsonSerde for data serialization.
 */
@Repository("mongoNodeRepository")
public class MongoNodeRepository implements ExtendedVersionedRepository<Node> {

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> edgesCollection;
    private final MongoCollection<Document> componentElementsCollection;
    private final Serde<String> serde = new JsonSerde();

    public MongoNodeRepository(final MongoDatabase database) {
        collection = database.getCollection("nodes");
        edgesCollection = database.getCollection("edges");
        componentElementsCollection = database.getCollection("component_elements");
    }

    @Override
    public Node save(final Node node) {
        return Io.withReturn(() -> {
            final var document = MongoHelper.createBaseDocument(
                    node.locator(), node.type().code(), node.created(), serde.serialize(node.data()));
            MongoHelper.addExpiryToDocument(document, node.expired());

            collection.insertOne(document);
            return node;
        });
    }

    @Override
    public Optional<Node> findActive(final NanoId nodeId) {
        final var document = collection
                .find(and(eq("id", nodeId.id()), not(exists("expired"))))
                .sort(descending("versionId"))
                .first();

        return Optional.ofNullable(document).map(this::documentToNode);
    }

    @Override
    public List<Node> findAll(final NanoId nodeId) {
        final var documents = collection.find(eq("id", nodeId.id())).sort(ascending("versionId"));

        return StreamSupport.stream(documents.spliterator(), false)
                .map(this::documentToNode)
                .toList();
    }

    @Override
    public Optional<Node> find(final Locator locator) {
        final var document = collection
                .find(and(eq("id", locator.id().id()), eq("versionId", locator.version())))
                .first();

        return Optional.ofNullable(document).map(this::documentToNode);
    }

    @Override
    public Optional<Node> findAt(final NanoId nodeId, final Instant timestamp) {
        final var timestampStr = timestamp.truncatedTo(ChronoUnit.MILLIS).toString();
        final var document = collection
                .find(and(
                        eq("id", nodeId.id()),
                        lte("created", timestampStr),
                        or(not(exists("expired")), gt("expired", timestampStr))))
                .sort(descending("versionId"))
                .first();

        return Optional.ofNullable(document).map(this::documentToNode);
    }

    @Override
    public boolean delete(final NanoId nodeId) {
        final var result = collection.deleteMany(eq("id", nodeId.id()));
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean expire(final NanoId elementId, final Instant expiredAt) {
        final var result = collection.updateMany(
                and(eq("id", elementId.id()), not(exists("expired"))),
                new Document(
                        "$set",
                        new Document(
                                "expired",
                                expiredAt.truncatedTo(ChronoUnit.MILLIS).toString())));
        return result.getModifiedCount() > 0;
    }

    private Node documentToNode(final Document document) {
        return Io.withReturn(() -> {
            final var versionedData = MongoHelper.extractVersionedData(document);
            final var data = serde.deserialize(versionedData.serializedData());
            final var type = new SimpleType(versionedData.type());

            // Find edges where this node is the source or target
            final var nodeId = versionedData.locator().id().id();
            final var edgeRefs = new ArrayList<Reference<Edge>>();

            // Find outgoing edges (where this node is the source)
            final var outgoingEdges = edgesCollection.find(and(eq("sourceId", nodeId), not(exists("expired"))));

            for (final var edgeDoc : outgoingEdges) {
                final var edgeId = new NanoId(edgeDoc.getString("id"));
                final var edgeVersion = edgeDoc.getInteger("versionId");
                final var edgeLocator = new Locator(edgeId, edgeVersion);
                edgeRefs.add(new Reference.Unloaded<>(edgeLocator, Edge.class));
            }

            // Find incoming edges (where this node is the target)
            final var incomingEdges = edgesCollection.find(and(eq("targetId", nodeId), not(exists("expired"))));

            for (final var edgeDoc : incomingEdges) {
                final var edgeId = new NanoId(edgeDoc.getString("id"));
                final var edgeVersion = edgeDoc.getInteger("versionId");
                final var edgeLocator = new Locator(edgeId, edgeVersion);
                edgeRefs.add(new Reference.Unloaded<>(edgeLocator, Edge.class));
            }

            // Find components containing this node
            final var componentRefs = new HashSet<Reference<Component>>();
            final var componentDocs =
                    componentElementsCollection.find(and(eq("elementId", nodeId), eq("elementType", "node")));

            for (final var compDoc : componentDocs) {
                final var componentId = new NanoId(compDoc.getString("componentId"));
                final var componentVersion = compDoc.getInteger("componentVersionId");
                final var componentLocator = new Locator(componentId, componentVersion);
                componentRefs.add(new Reference.Unloaded<>(componentLocator, Component.class));
            }

            return new SimpleNode(
                    versionedData.locator(),
                    type,
                    edgeRefs,
                    data,
                    versionedData.created(),
                    versionedData.expired(),
                    componentRefs);
        });
    }

    @Override
    public List<NanoId> allIds() {
        return MongoHelper.convertToNanoIdList(collection.distinct("id", String.class));
    }

    @Override
    public List<NanoId> allActiveIds() {
        return MongoHelper.convertToNanoIdList(collection.distinct("id", not(exists("expired")), String.class));
    }
}
