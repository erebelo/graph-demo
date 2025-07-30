/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.sqllite;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Edge;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.Reference;
import com.erebelo.graphdemo.model.serde.PropertiesSerde;
import com.erebelo.graphdemo.model.serde.Serde;
import com.erebelo.graphdemo.model.simple.SimpleNode;
import com.erebelo.graphdemo.model.simple.SimpleType;
import com.erebelo.graphdemo.persistence.ExtendedVersionedRepository;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SQLite implementation of NodeRepository.
 */
@Repository("sqliteNodeRepository")
public final class SqliteNodeRepository implements ExtendedVersionedRepository<Node> {

    private final Serde<Map<String, Object>> serde = new PropertiesSerde();
    private final SqliteHandleProvider session;

    public SqliteNodeRepository(final SqliteHandleProvider session) {
        this.session = session;
    }

    private Handle getHandle() {

        return session.handle();
    }

    @Override
    public Node save(final Node node) {
        final var sql =
                """
                        INSERT INTO node (id, version_id, type, created, expired)
                        VALUES (:id, :version_id, :type, :created, :expired)
                        """;

        Io.withVoid(() -> {
            getHandle()
                    .createUpdate(sql)
                    .bind("id", node.locator().id().id())
                    .bind("version_id", node.locator().version())
                    .bind("type", node.type().code())
                    .bind("created", node.created().toString())
                    .bind("expired", node.expired().map(Object::toString).orElse(null))
                    .execute();

            // Save properties in separate table
            saveProperties(node.locator().id(), node.locator().version(), node.data());
        });

        return node;
    }

    @Override
    public Optional<Node> findActive(final NanoId nodeId) {
        final var sql = "SELECT * FROM node WHERE id = :id AND expired IS NULL ORDER BY version_id DESC LIMIT 1";

        return Io.withReturn(() -> getHandle()
                .createQuery(sql)
                .bind("id", nodeId.id())
                .map(new NodeMapper())
                .findOne());
    }

    @Override
    public List<Node> findAll(final NanoId nodeId) {
        final var sql = "SELECT * FROM node WHERE id = :id ORDER BY version_id";

        return Io.withReturn(() -> getHandle()
                .createQuery(sql)
                .bind("id", nodeId.id())
                .map(new NodeMapper())
                .list());
    }

    @Override
    public Optional<Node> find(final Locator locator) {
        final var sql = "SELECT * FROM node WHERE id = :id AND version_id = :version_id";

        return Io.withReturn(() -> getHandle()
                .createQuery(sql)
                .bind("id", locator.id().id())
                .bind("version_id", locator.version())
                .map(new NodeMapper())
                .findOne());
    }

    @Override
    public Optional<Node> findAt(final NanoId nodeId, final Instant timestamp) {
        final var sql =
                """
                        SELECT * FROM node
                        WHERE id = :id AND created <= :timestamp
                              AND (expired IS NULL OR expired > :timestamp)
                        ORDER BY version_id DESC
                        LIMIT 1
                        """;

        return Io.withReturn(() -> {
            final var timestampStr = timestamp.toString();
            return getHandle()
                    .createQuery(sql)
                    .bind("id", nodeId.id())
                    .bind("timestamp", timestampStr)
                    .map(new NodeMapper())
                    .findOne();
        });
    }

    @Override
    public boolean delete(final NanoId nodeId) {
        final var sql = "DELETE FROM node WHERE id = :id";

        return Io.withReturn(
                () -> getHandle().createUpdate(sql).bind("id", nodeId.id()).execute() > 0);
    }

    @Override
    public boolean expire(final NanoId elementId, final Instant expiredAt) {
        final var sql = "UPDATE node SET expired = :expired WHERE id = :id AND expired IS NULL";

        return Io.withReturn(() -> getHandle()
                .createUpdate(sql)
                .bind("expired", expiredAt.toString())
                .bind("id", elementId.id())
                .execute()
                > 0);
    }

    @Override
    public List<NanoId> allIds() {
        final var sql = "SELECT DISTINCT id FROM node";

        return Io.withReturn(() -> getHandle()
                .createQuery(sql)
                .map((rs, ctx) -> new NanoId(rs.getString("id")))
                .list());
    }

    @Override
    public List<NanoId> allActiveIds() {
        final var sql = "SELECT DISTINCT id FROM node WHERE expired IS NULL";

        return Io.withReturn(() -> getHandle()
                .createQuery(sql)
                .map((rs, ctx) -> new NanoId(rs.getString("id")))
                .list());
    }

    private class NodeMapper implements RowMapper<Node> {

        @Override
        public final Node map(final ResultSet rs, final StatementContext ctx) throws SQLException {

            final var id = new NanoId(rs.getString("id"));
            final var versionId = rs.getInt("version_id");
            final var type = new SimpleType(rs.getString("type"));
            final var created = Instant.parse(rs.getString("created"));

            Optional<Instant> expired = Optional.empty();
            final var expiredStr = rs.getString("expired");
            if (expiredStr != null) {
                expired = Optional.of(Instant.parse(expiredStr));
            }

            final var data = loadProperties(id, versionId);
            final var locator = new Locator(id, versionId);

            // Find edges where this node is the source or target
            final var edgeRefs = new HashSet<Reference<Edge>>();

            // Find outgoing edges (where this node is the source)
            final var outgoingSql =
                    "SELECT DISTINCT id, version_id FROM edge WHERE source_id = :nodeId AND source_version_id = :versionId AND expired IS NULL";
            getHandle()
                    .createQuery(outgoingSql)
                    .bind("nodeId", id.id())
                    .bind("versionId", versionId)
                    .map((edgeRs, edgeCtx) -> {
                        final var edgeId = new NanoId(edgeRs.getString("id"));
                        final var edgeVersion = edgeRs.getInt("version_id");
                        final var edgeLocator = new Locator(edgeId, edgeVersion);
                        edgeRefs.add(new Reference.Unloaded<>(edgeLocator, Edge.class));
                        return null;
                    })
                    .list();

            // Find incoming edges (where this node is the target)
            final var incomingSql =
                    "SELECT DISTINCT id, version_id FROM edge WHERE target_id = :nodeId AND target_version_id = :versionId AND expired IS NULL";
            getHandle()
                    .createQuery(incomingSql)
                    .bind("nodeId", id.id())
                    .bind("versionId", versionId)
                    .map((edgeRs, edgeCtx) -> {
                        final var edgeId = new NanoId(edgeRs.getString("id"));
                        final var edgeVersion = edgeRs.getInt("version_id");
                        final var edgeLocator = new Locator(edgeId, edgeVersion);
                        edgeRefs.add(new Reference.Unloaded<>(edgeLocator, Edge.class));
                        return null;
                    })
                    .list();

            // Find components containing this node
            final var componentRefs = new HashSet<Reference<Component>>();
            final var componentSql =
                    "SELECT DISTINCT component_id, component_version FROM component_element WHERE element_id = :nodeId AND element_version = " +
                            ":versionId AND element_type = 'SimpleNode'";
            getHandle()
                    .createQuery(componentSql)
                    .bind("nodeId", id.id())
                    .bind("versionId", versionId)
                    .map((compRs, compCtx) -> {
                        final var componentId = new NanoId(compRs.getString("component_id"));
                        final var componentVersion = compRs.getInt("component_version");
                        final var componentLocator = new Locator(componentId, componentVersion);
                        componentRefs.add(new Reference.Unloaded<>(componentLocator, Component.class));
                        return null;
                    })
                    .list();

            return new SimpleNode(locator, type, new ArrayList<>(edgeRefs), data, created, expired, componentRefs);
        }
    }

    private void saveProperties(final NanoId nodeId, final int version, final Data data) {
        final var sql =
                """
                        INSERT INTO node_properties (id, version_id, property_key, property_value)
                        VALUES (:id, :version_id, :property_key, :property_value)
                        """;

        final var properties = serde.serialize(data);
        Io.withVoid(() -> {
            final var batch = getHandle().prepareBatch(sql);
            for (final var entry : properties.entrySet()) {
                batch.bind("id", nodeId.id())
                        .bind("version_id", version)
                        .bind("property_key", entry.getKey())
                        .bind("property_value", String.valueOf(entry.getValue()))
                        .add();
            }
            batch.execute();
        });
    }

    private Data loadProperties(final NanoId nodeId, final int version) {
        final var sql =
                """
                        SELECT property_key, property_value
                        FROM node_properties
                        WHERE id = :id AND version_id = :version_id
                        """;

        return Io.withReturn(() -> {
            final var properties = new HashMap<String, Object>();
            getHandle()
                    .createQuery(sql)
                    .bind("id", nodeId.id())
                    .bind("version_id", version)
                    .map((rs, ctx) -> {
                        properties.put(rs.getString("property_key"), rs.getString("property_value"));
                        return null;
                    })
                    .list();
            return serde.deserialize(properties);
        });
    }
}
