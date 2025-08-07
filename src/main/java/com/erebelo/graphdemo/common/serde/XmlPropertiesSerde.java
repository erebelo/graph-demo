/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.serde;

import com.erebelo.graphdemo.common.fp.Io;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Serialization methods for a java.util.Properties object.
 */
public final class XmlPropertiesSerde {

    /**
     * Type contains only static members.
     */
    private XmlPropertiesSerde() {
    }

    /**
     * Parses the local filesystem URI into a map of string key/value pairs. The XML
     * file should be in standard java.util.Properties format.
     */
    public static Map<String, String> deserialize(final Path path) {

        return Io.withReturn(() -> {
            try (InputStream in = new FileInputStream(path.toFile())) {
                return deserialize(in);
            }
        });
    }

    /**
     * Parses the specified stream into a map of string key/value pairs. The XML
     * file should be in standard java.util.Properties format.
     */
    public static Map<String, String> deserialize(final InputStream in) {

        final var props = new Properties();
        Io.withVoid(() -> props.loadFromXML(in));
        return props.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
    }

    /**
     * Outputs the specified map as XML properties to the filesystem URI specified.
     */
    public static void serialize(final Map<String, String> map, final Path target) {

        Io.withVoid(() -> {
            try (OutputStream out = new FileOutputStream(target.toFile())) {
                serialize(map, out);
            }
        });
    }

    /**
     * Outputs the specified map as a XML properties.
     */
    public static void serialize(final Map<String, String> map, final OutputStream out) {

        final var props = new Properties();
        props.putAll(map);
        Io.withVoid(() -> props.storeToXML(out, null));
    }
}
