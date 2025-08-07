/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.api.impl;

import com.erebelo.graphdemo.api.ComponentService;
import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Element;
import com.erebelo.graphdemo.model.jgrapht.ComponentOperations;
import com.erebelo.graphdemo.persistence.GraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of ComponentService using session-based transactions.
 */
@Service
public final class DefaultComponentService implements ComponentService {

    private final GraphRepository repository;

    private final ComponentOperations componentOperations;

    public DefaultComponentService(final GraphRepository repository, final ComponentOperations componentOperations) {

        this.repository = repository;
        this.componentOperations = componentOperations;
    }

    @Override
    @Transactional
    public Component add(final List<Element> elements, final Data data) {

        final var component = componentOperations.add(elements, data, Instant.now());
        return repository.components().save(component);
    }

    @Override
    @Transactional
    public Component update(final NanoId id, final List<Element> elements, final Data data) {

        final var component = componentOperations.update(id, elements, data, Instant.now());
        repository.components().save(component);
        return component;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Component> findActiveContaining(final NanoId id) {

        // Find all active components and check if they contain an element with this ID
        return componentOperations.allActive().stream()
                .filter(component -> component.elements().stream()
                        .anyMatch(element -> element.locator().id().equals(id)
                                && element.expired().isEmpty()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Component> findContaining(final NanoId id, final Instant timestamp) {

        final var allComponents = componentOperations.allActive().stream()
                .flatMap(component ->
                        componentOperations.findAllVersions(component.locator().id()).stream())
                .toList();
        return allComponents.stream()
                .filter(component -> !component.created().isAfter(timestamp)
                        && (component.expired().isEmpty()
                        || component.expired().get().isAfter(timestamp))
                        && component.elements().stream()
                        .anyMatch(element -> element.locator().id().equals(id)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Component find(final Locator locator) {

        return repository
                .components()
                .find(locator)
                .orElseThrow(() -> new IllegalArgumentException("Component not found: " + locator));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Component> findActive(final NanoId id) {

        return repository.components().findActive(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Component> findAt(final NanoId id, final Instant timestamp) {

        return repository.components().findAt(id, timestamp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Component> findAllVersions(final NanoId id) {

        return repository.components().findAll(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NanoId> allActive() {

        return repository.components().allActiveIds();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NanoId> all() {

        return repository.components().allIds();
    }

    @Override
    @Transactional
    public Optional<Component> expire(final NanoId id) {

        final var activeComponent = componentOperations.findActive(id);
        if (activeComponent.isPresent()) {
            final var expired = componentOperations.expire(id, Instant.now());
            if (expired.expired().isEmpty()) {
                throw new IllegalStateException("Expired component is missing an expiration timestamp");
            }
            repository.components().expire(id, expired.expired().get());
            return Optional.of(expired);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean delete(final NanoId id) {

        return repository.components().delete(id);
    }
}
