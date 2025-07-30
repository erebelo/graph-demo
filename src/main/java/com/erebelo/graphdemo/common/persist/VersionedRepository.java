package com.erebelo.graphdemo.common.persist;

import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.common.version.Versioned;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Base operations for any repository that tracks a versioned element (Node, Edge and Component).
 */
public interface VersionedRepository<T extends Versioned> {

    /**
     * Saves an element to the persistence store.
     */
    T save(T node);

    /**
     * Finds an element by its ID returning all versions (active and inactive).
     */
    List<T> findAll(NanoId nodeId);

    /**
     * Finds the active element (if present) for the specified ID.
     */
    Optional<T> findActive(NanoId nodeId);

    /**
     * Finds a specific version of an element by its ID and version.
     */
    Optional<T> find(Locator locator);

    /**
     * Finds an element by its ID at the specified timestamp (if it exists).
     */
    Optional<T> findAt(NanoId nodeId, Instant timestamp);

    /**
     * Deletes an element from the repository returning true if it was found.
     */
    boolean delete(NanoId nodeId);

    /**
     * Expires an element at the given timestamp returning true if it was found.
     */
    boolean expire(NanoId id, Instant timestamp);
}
