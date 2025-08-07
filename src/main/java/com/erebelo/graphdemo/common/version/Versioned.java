package com.erebelo.graphdemo.common.version;

import com.erebelo.graphdemo.common.annotation.Stable;
import java.time.Instant;
import java.util.Optional;

/**
 * Represents a versioned item that can be located by a unique NanoId and
 * version number.
 */
@Stable
public interface Versioned extends Locateable {

    /**
     * Returns the timestamp when this version was created.
     */
    Instant created();

    /**
     * Returns the timestamp when this version expired, if applicable.
     */
    Optional<Instant> expired();
}
