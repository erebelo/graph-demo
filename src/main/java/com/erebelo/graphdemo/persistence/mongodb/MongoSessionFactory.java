/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.mongodb;

import com.erebelo.graphdemo.persistence.Session;
import com.erebelo.graphdemo.persistence.SessionFactory;
import com.mongodb.client.MongoClients;

/**
 * MongoDB implementation of SessionFactory.
 */
public final class MongoSessionFactory implements SessionFactory {

    private final String connectionString;
    private final String databaseName;

    public MongoSessionFactory(final String connectionString, final String databaseName) {

        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    @Override
    public Session create() {

        final var mongoClient = MongoClients.create(connectionString);
        return new MongoSession(mongoClient, databaseName);
    }
}
