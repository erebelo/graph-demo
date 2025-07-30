/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.resource;

import java.io.InputStream;
import java.util.Optional;

/**
 * Helper class to assist in resolving resources from the classpath to an InputStream. All the classloading methods in this type utilize
 * Class#getResource() rather than ClassLoader#getResource(). Therefore the paths may be either relative or absolute.
 */
public final class StreamResourceHelper {

    /**
     * Type contains only static members.
     */
    private StreamResourceHelper() {
    }

    /**
     * Attempts to resolve a stream with the content of a resource from the curent classpath, relative to this class. If the resource does not exist,
     * an IllegalArgumentException is thrown.
     */
    public static InputStream requireStreamFromClasspath(final String path) {

        return requireStreamFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve a stream with the content of a resource from the curent classpath, relative to the specified caller. If the resource does
     * not exist, an IllegalArgumentException is thrown.
     */
    public static InputStream requireStreamFromClasspath(final String path, final Class<?> caller) {

        final var value = resolveStreamFromClasspath(path, caller);
        return UriResourceHelper.require(path, value);
    }

    /**
     * Attempts to resolve a stream with the content of a resource from the curent classpath, relative to this class.
     */
    public static Optional<InputStream> resolveStreamFromClasspath(final String path) {

        return resolveStreamFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve a stream with the content of a resource from the curent classpath, relative to the specified caller.
     */
    public static Optional<InputStream> resolveStreamFromClasspath(final String path, final Class<?> caller) {

        return Optional.ofNullable(caller.getResourceAsStream(path));
    }
}
