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
import com.erebelo.graphdemo.model.Element;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.serde.JsonSerde;
import com.erebelo.graphdemo.model.serde.Serde;
import com.erebelo.graphdemo.model.simple.SimpleComponent;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
 * MongoDB implementation of ComponentRepository using JsonSerde for data serialization.
 */
@Repository("mongoComponentRepository")
public class MongoComponentRepository implements ExtendedVersionedRepository<Component> {

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> elementsCollection;
    private final Serde<String> serde = new JsonSerde();
    private final MongoNodeRepository nodeRepository;
    private final MongoEdgeRepository edgeRepository;

    public MongoComponentRepository(
            final MongoDatabase database,
            final MongoNodeRepository nodeRepository,
            final MongoEdgeRepository edgeRepository) {
        collection = database.getCollection("components");
        elementsCollection = database.getCollection("component_elements");
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
    public Component save(final Component component) {
        return Io.withReturn(() -> {
            final var document = MongoHelper.createBaseDocument(
                    component.locator(), "component", component.created(), serde.serialize(component.data()));
            MongoHelper.addExpiryToDocument(document, component.expired());

            collection.insertOne(document);

            // Save component elements relationships
            for (final var elementRef : component.elements()) {
                final var elementLocator = elementRef.locator();

                // Determine element type by checking if it's a loaded reference
                String elementType = "unknown";
                if (elementRef instanceof Reference.Loaded<?> loaded) {
                    elementType = (loaded.value() instanceof Node) ? "node" : "edge";
                } else if (elementRef instanceof Reference.Unloaded<?> unloaded) {
                    // For unloaded references, we'll need to check the type
                    elementType = unloaded.type().equals(Node.class) ? "node" : "edge";
                }

                final var elementDoc = new Document()
                        .append("componentId", component.locator().id().id())
                        .append("componentVersionId", component.locator().version())
                        .append("elementId", elementLocator.id().id())
                        .append("elementVersionId", elementLocator.version())
                        .append("elementType", elementType);

                elementsCollection.insertOne(elementDoc);
            }

            return component;
        });
    }

    @Override
    public Optional<Component> findActive(final NanoId componentId) {
        final var document = collection
                .find(and(eq("id", componentId.id()), not(exists("expired"))))
                .sort(descending("versionId"))
                .first();

        return Optional.ofNullable(document).map(this::documentToComponent);
    }

    @Override
    public List<Component> findAll(final NanoId componentId) {
        final var documents = collection.find(eq("id", componentId.id())).sort(ascending("versionId"));

        return StreamSupport.stream(documents.spliterator(), false)
                .map(this::documentToComponent)
                .toList();
    }

    @Override
    public Optional<Component> find(final Locator locator) {
        final var document = collection
                .find(and(eq("id", locator.id().id()), eq("versionId", locator.version())))
                .first();

        return Optional.ofNullable(document).map(this::documentToComponent);
    }

    @Override
    public Optional<Component> findAt(final NanoId componentId, final Instant timestamp) {
        final var timestampStr = timestamp.truncatedTo(ChronoUnit.MILLIS).toString();
        final var document = collection
                .find(and(
                        eq("id", componentId.id()),
                        lte("created", timestampStr),
                        or(not(exists("expired")), gt("expired", timestampStr))))
                .sort(descending("versionId"))
                .first();

        return Optional.ofNullable(document).map(this::documentToComponent);
    }

    @Override
    public boolean delete(final NanoId componentId) {
        final var componentResult = collection.deleteMany(eq("id", componentId.id()));
        final var elementsResult = elementsCollection.deleteMany(eq("componentId", componentId.id()));
        return componentResult.getDeletedCount() > 0;
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

    private Component documentToComponent(final Document document) {
        return Io.withReturn(() -> {
            final var versionedData = MongoHelper.extractVersionedData(document);
            final var data = serde.deserialize(versionedData.serializedData());

            final var elements = new ArrayList<Reference<Element>>();
            final var elementDocs = elementsCollection.find(and(
                    eq("componentId", versionedData.locator().id().id()),
                    eq("componentVersionId", versionedData.locator().version())));

            for (final var elementDoc : elementDocs) {
                final var elementId = new NanoId(elementDoc.getString("elementId"));
                final var elementVersionId = elementDoc.getInteger("elementVersionId");
                final var elementLocator = new Locator(elementId, elementVersionId);
                final var elementType = elementDoc.getString("elementType");

                if ("node".equals(elementType)) {
                    nodeRepository
                            .find(elementLocator)
                            .ifPresent(node -> elements.add(new Reference.Loaded<Element>(node)));
                } else if ("edge".equals(elementType)) {
                    edgeRepository
                            .find(elementLocator)
                            .ifPresent(edge -> elements.add(new Reference.Loaded<Element>(edge)));
                }
            }

            return new SimpleComponent(
                    versionedData.locator(), elements, data, versionedData.created(), versionedData.expired());
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
