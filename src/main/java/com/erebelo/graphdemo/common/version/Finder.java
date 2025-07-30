package com.erebelo.graphdemo.common.version;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Interface to locate elements and components contained in a graph.
 */
public interface Finder<T extends Versioned> {

    List<T> findVersions(NanoId id);

    Optional<T> findActive(NanoId id);

    T find(Locator locator);

    Optional<T> findAt(NanoId id, Instant timestamp);
}
