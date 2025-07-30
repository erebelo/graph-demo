/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.serde;

import com.erebelo.graphdemo.common.error.IoException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Helper methods to serialize and deserialize an object.
 */
public final class JavaSerde {

    /**
     * Type contains only static members.
     */
    private JavaSerde() {
    }

    /**
     * Serializes the specified object to the target URI.
     */
    public static void serialize(final Serializable target, final Path path) {

        try (var out = new FileOutputStream(path.toFile())) {
            serialize(target, out);
        } catch (final IOException e) {
            throw new IoException("Error evaluating URI %s for output".formatted(path), e);
        }
    }

    /**
     * Serializes the specified object to the output stream.
     */
    public static void serialize(final Serializable target, final OutputStream out) {

        try (ObjectOutput object = new ObjectOutputStream(out)) {
            object.writeObject(target);
        } catch (final IOException e) {
            throw new IoException("Error serializing %s".formatted(target.getClass()), e);
        }
    }

    /**
     * Deserializes the specified type of object found at the indicated URI.
     */
    public static <T extends Serializable> T deserialize(final Path path, final Class<T> target) {

        try (var in = new FileInputStream(path.toFile())) {
            return deserialize(in, target);
        } catch (final IOException e) {
            throw new IoException("Error evaluating URI %s for input".formatted(path), e);
        }
    }

    /**
     * Deserializes the data in the specified stream, returning the target type requested.
     */
    public static <T extends Serializable> T deserialize(final InputStream in, final Class<T> target) {

        try (ObjectInput object = new ObjectInputStream(in)) {
            return target.cast(object.readObject());
        } catch (final ClassNotFoundException notPossible) {
            throw new IoException("Class not found %s".formatted(target), notPossible);
        } catch (final IOException e) {
            throw new IoException("Error deserializing %s".formatted(target), e);
        }
    }
}
