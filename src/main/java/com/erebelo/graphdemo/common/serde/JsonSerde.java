/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.serde;

import com.erebelo.graphdemo.common.fp.Io;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Supplier;

/**
 * Allows for serialization and desrialization of objects to and from JSON. This
 * types abstracts away the underlying implementation.
 */
public final class JsonSerde {

    /**
     * Type contains only static methods.
     */
    private JsonSerde() {
    }

    /**
     * Deserializes from JSON.
     */
    public static <T> T fromJson(final String json, final Class<T> target) {

        return Io.withReturn(() -> createMapper().readValue(json, target));
    }

    /**
     * Deserializes from JSON.
     */
    public static <T> T fromJson(final Reader reader, final Class<T> target) {

        return Io.withReturn(() -> createMapper().readValue(reader, target));
    }

    /**
     * Deserializes from JSON.
     */
    public static <T> T fromJson(final Supplier<? extends Reader> fx, final Class<T> target) {

        return Io.withReturn(() -> {
            try (var reader = fx.get()) {
                return fromJson(reader, target);
            }
        });
    }

    /**
     * Serializes out to JSON.
     */
    public static String toJson(final Object target) {

        return Io.withReturn(() -> createMapper().writeValueAsString(target));
    }

    /**
     * Serializes as JSON writing to the specified writer.
     */
    public static void toJson(final Object target, final Writer writer) {

        Io.withVoid(() -> createMapper().writeValue(writer, target));
    }

    /**
     * Serializes as JSON writing to the specified lazy writer.
     */
    public static void toJson(final Object target, final Supplier<? extends Writer> fx) {

        Io.withVoid(() -> {
            try (var writer = fx.get()) {
                toJson(target, writer);
            }
        });
    }

    /**
     * Attempts to remove JSON symbols and delimiters to output JSON that could not
     * be parsed (either came in as an unexpected type or is invalid format).
     */
    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    public static String prettify(final String maybeJson) {

        return maybeJson.replaceAll("[{}\"]", "").replaceAll(",", "\n").replaceAll(":", " -");
    }

    /**
     * Creates an object mapper to use.
     *
     * @return ObjectMapper Mapper to use
     */
    private static ObjectMapper createMapper() {

        final var mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new ParameterNamesModule());
        return mapper;
    }
}
