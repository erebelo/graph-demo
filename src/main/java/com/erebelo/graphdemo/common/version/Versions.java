package com.erebelo.graphdemo.common.version;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Helper utilities for graph element operations.
 */
public final class Versions {

    /**
     * Type contains only static members.
     */
    private Versions() {
    }

    /**
     * Finds the active version of an element by ID.
     */
    public static <E extends Versioned> Optional<E> findActive(final NanoId id, final Collection<E> items) {

        return items.stream()
                .filter(v -> v.locator().id().equals(id))
                .filter(v -> v.expired().isEmpty())
                .max(Comparator.comparing(e -> e.locator().version()));
    }

    /**
     * Finds the version of an element active at a specific timestamp.
     */
    public static <E extends Versioned> Optional<E> findAt(
            final NanoId id, final Instant timestamp, final Collection<E> items) {

        return items.stream()
                .filter(v -> v.locator().id().equals(id))
                .filter(v -> isActiveAt(timestamp, v))
                .max(Comparator.comparing(e -> e.locator().version()));
    }

    /**
     * Finds all versions of an element by ID.
     */
    public static <E extends Versioned> List<E> findAllVersions(final NanoId id, final Collection<E> items) {

        return items.stream()
                .filter(v -> v.locator().id().equals(id))
                .sorted(Comparator.comparing(e -> e.locator().version()))
                .toList();
    }

    /**
     * Returns all active elements.
     */
    public static <E extends Versioned> List<E> allActive(final Collection<E> items) {

        return items.stream().filter(v -> v.expired().isEmpty()).toList();
    }

    /**
     * Validates that an element can be expired.
     */
    public static <E extends Versioned> E validateForExpiry(
            final Optional<E> element, final NanoId id, final String elementType) {

        return element.orElseThrow(() -> new IllegalArgumentException(elementType + " not found: " + id));
    }

    /**
     * Checks if an identifiable item is active at a given timestamp.
     */
    public static boolean isActiveAt(final Instant timestamp, final Versioned e) {

        final var created = e.created();
        final var expired = e.expired();
        return !created.isAfter(timestamp)
                && (expired.isEmpty() || expired.get().isAfter(timestamp));
    }
}
