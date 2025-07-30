/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.tinkerpop;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractTransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

/**
 * Spring transaction manager for Apache Tinkerpop graphs.
 */
public final class TinkerpopTransactionManager implements PlatformTransactionManager {

    private final Supplier<Graph> graphSupplier;
    private static final String GRAPH_TRANSACTION_KEY = "tinkerpop.graph.transaction";

    public TinkerpopTransactionManager(final Supplier<Graph> graphSupplier) {

        this.graphSupplier = graphSupplier;
    }

    @Override
    public TransactionStatus getTransaction(final TransactionDefinition definition) throws TransactionException {

        final var graph = graphSupplier.get();

        // Check if graph supports transactions
        if (!graph.features().graph().supportsTransactions()) {
            // Return a simple status for graphs without transaction support
            return new SimpleTransactionStatus(false);
        }

        // Check if we already have an active transaction
        final var existingTransaction = TransactionSynchronizationManager.getResource(GRAPH_TRANSACTION_KEY);
        if (existingTransaction != null) {
            // Return existing transaction
            return new SimpleTransactionStatus(false);
        }

        // Start a new transaction
        final var tx = graph.tx();
        tx.open();

        // Store transaction in thread-local
        TransactionSynchronizationManager.bindResource(GRAPH_TRANSACTION_KEY, tx);

        return new TinkerpopTransactionStatus(tx, true);
    }

    @Override
    public void commit(final TransactionStatus status) throws TransactionException {

        if (status instanceof TinkerpopTransactionStatus txStatus && txStatus.isNewTransaction()) {
            try {
                txStatus.getTransaction().commit();
            } finally {
                TransactionSynchronizationManager.unbindResource(GRAPH_TRANSACTION_KEY);
            }
        }
    }

    @Override
    public void rollback(final TransactionStatus status) throws TransactionException {

        if (status instanceof TinkerpopTransactionStatus txStatus && txStatus.isNewTransaction()) {
            try {
                txStatus.getTransaction().rollback();
            } finally {
                TransactionSynchronizationManager.unbindResource(GRAPH_TRANSACTION_KEY);
            }
        }
    }

    /**
     * Transaction status implementation for Tinkerpop.
     */
    private static final class TinkerpopTransactionStatus extends AbstractTransactionStatus {

        private final Transaction transaction;
        private final boolean newTransaction;

        TinkerpopTransactionStatus(final Transaction transaction, final boolean newTransaction) {

            this.transaction = transaction;
            this.newTransaction = newTransaction;
        }

        Transaction getTransaction() {

            return transaction;
        }

        @Override
        public boolean isNewTransaction() {

            return newTransaction;
        }
    }
}
