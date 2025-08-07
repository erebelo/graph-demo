/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence;

import com.erebelo.graphdemo.common.persist.VersionedRepository;
import com.erebelo.graphdemo.common.version.NanoId;
import com.erebelo.graphdemo.common.version.Versioned;
import java.util.List;

/**
 * Extended versioned repository with additional operations for retrieving all
 * IDs.
 */
public interface ExtendedVersionedRepository<T extends Versioned> extends VersionedRepository<T> {

    /**
     * Returns all unique IDs in this repository.
     */
    List<NanoId> allIds();

    /**
     * Returns all unique IDs that have active versions.
     */
    List<NanoId> allActiveIds();
}
