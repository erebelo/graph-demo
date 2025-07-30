/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.simple;

import com.erebelo.graphdemo.common.version.Locator;
import com.erebelo.graphdemo.model.Component;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.Element;
import com.erebelo.graphdemo.model.Reference;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record SimpleComponent(
        Locator locator, List<Reference<Element>> elements, Data data, Instant created, Optional<Instant> expired)
        implements Component {
}
