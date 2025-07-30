/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.sqllite;

import com.erebelo.graphdemo.persistence.GraphRepository;

/**
 * Graph listener repository using an in memory SQLite implementation.
 */
//@Repository("sqliteGraphRepository")
public record SqliteGraphRepository(
        SqliteNodeRepository nodes, SqliteEdgeRepository edges, SqliteComponentRepository components)
        implements GraphRepository {

    public static GraphRepository create(final SqliteHandleProvider session) {

        final var nodeRepository = new SqliteNodeRepository(session);
        final var edgeRepository = new SqliteEdgeRepository(session, nodeRepository);
        return new SqliteGraphRepository(
                nodeRepository, edgeRepository, new SqliteComponentRepository(session, nodeRepository, edgeRepository));
    }
}
