/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.resource;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Helper class to assist in resolving resources from the classpath to a Path. All the classloading methods in this type utilize Class#getResource()
 * rather than ClassLoader#getResource(). Therefore the paths may be either relative or absolute.
 */
public final class PathResourceHelper {

    /**
     * Type contains only static members.
     */
    private PathResourceHelper() {
    }

    /**
     * Attempts to resolve the Path of a resource from the curent classpath relative to this class. If not found, an IllegalArgumentException will be
     * thrown.
     */
    public static Path requirePathFromClasspath(final String path) {

        return requirePathFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve the Path of a resource from the curent classpath, relative to the calling class. If not found, an IllegalArgumentException
     * will be thrown.
     */
    public static Path requirePathFromClasspath(final String path, final Class<?> caller) {

        final var value = resolvePathFromClasspath(path, caller);
        return UriResourceHelper.require(path, value);
    }

    /**
     * Attempts to resolve the Path of a resource from the curent classpath, relative to this class.
     */
    public static Optional<Path> resolvePathFromClasspath(final String path) {

        return resolvePathFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve the Path of a resource from the curent classpath, relative to the calling class.
     */
    public static Optional<Path> resolvePathFromClasspath(final String path, final Class<?> caller) {

        final var value = UriResourceHelper.resolveUriFromClasspath(path, caller);
        return value.map(Path::of);
    }
}
