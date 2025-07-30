/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.model.serde;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.fp.Try;
import com.erebelo.graphdemo.model.Data;
import com.erebelo.graphdemo.model.simple.SimpleData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serde implementation that converts Data to and from JSON format.
 */
public final class JsonSerde implements Serde<String> {

    private static final String TYPE_FIELD = "_type";
    private static final String VALUE_FIELD = "_value";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes Data to JSON format including type information.
     */
    @Override
    public String serialize(final Data target) {
        return Try.withReturn(() -> {
            final var wrapper = objectMapper.createObjectNode();
            wrapper.put(TYPE_FIELD, target.javaClass().getName());
            wrapper.set(VALUE_FIELD, objectMapper.valueToTree(target.value()));
            return objectMapper.writeValueAsString(wrapper);
        });
    }

    /**
     * Deserializes JSON to Data, restoring type information.
     */
    @Override
    public Data deserialize(final String target) {
        return Try.withReturn(() -> {
            final var targetNode = objectMapper.readTree(target);
            final var typeName = targetNode.get(TYPE_FIELD).asText();
            final var valueNode = targetNode.get(VALUE_FIELD);
            final var type = Io.withReturn(() -> Class.forName(typeName));
            final var value = objectMapper.treeToValue(valueNode, type);
            return new SimpleData(type, value);
        });
    }
}
