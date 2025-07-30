/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.api.impl;

import com.erebelo.graphdemo.api.NodeService;
import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Node;
import com.erebelo.graphdemo.model.jgrapht.NodeOperations;
import com.erebelo.graphdemo.persistence.GraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of NodeService using Spring transactions.
 */
@Service
public final class DefaultNodeService implements NodeService {

    private final GraphRepository repository;
    private final NodeOperations nodeOperations;

    public DefaultNodeService(final GraphRepository repository, final NodeOperations nodeOperations) {

        this.repository = repository;
        this.nodeOperations = nodeOperations;
    }

    @Override
    @Transactional
    public Node add(final Data data) {

        final var node = nodeOperations.add(data, Instant.now());
        return repository.nodes().save(node);
    }

    @Override
    @Transactional
    public Node update(final NanoId id, final Data data) {

        final var node = nodeOperations.update(id, data, Instant.now());
        repository.nodes().save(node);
        return node;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Node> getNeighbors(final NanoId nodeId) {

        final var node = nodeOperations
                .findActive(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + nodeId));
        return nodeOperations.getNeighbors(node);
    }

    @Override
    @Transactional(readOnly = true)
    public Node find(final Locator locator) {

        return repository
                .nodes()
                .find(locator)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + locator));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Node> findActive(final NanoId id) {

        return repository.nodes().findActive(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Node> findAt(final NanoId id, final Instant timestamp) {

        return repository.nodes().findAt(id, timestamp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Node> findAllVersions(final NanoId id) {

        return repository.nodes().findAll(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NanoId> allActive() {

        return repository.nodes().allActiveIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NanoId> all() {

        return repository.nodes().allIds();
    }

    @Override
    @Transactional
    public Optional<Node> expire(final NanoId id) {

        final var activeNode = nodeOperations.findActive(id);
        if (activeNode.isPresent()) {
            final var expired = nodeOperations.expire(id, Instant.now());
            if (expired.expired().isEmpty()) {
                throw new IllegalStateException("Expired node is missing an expiration timestamp");
            }
            repository.nodes().expire(id, expired.expired().get());
            return Optional.of(expired);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean delete(final NanoId id) {

        return repository.nodes().delete(id);
    }
}
