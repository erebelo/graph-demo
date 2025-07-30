package com.erebelo.graphdemo.common.persist;

/**
 * Represents a transactional session for graph operations.
 */
public interface Session extends AutoCloseable {

    /**
     * Commits the current transaction.
     */
    void commit();

    /**
     * Rolls back the current transaction.
     */
    void rollback();

    /**
     * {@inheritDoc}
     */
    @Override
    void close();
}
