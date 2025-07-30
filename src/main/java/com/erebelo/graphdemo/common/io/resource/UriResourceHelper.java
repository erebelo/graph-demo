/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.resource;

import com.erebelo.graphdemo.common.fp.Fn1;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

/**
 * Helper class to assist in resolving resources from the classpath to a URI. All the classloading methods in this type utilize Class#getResource()
 * rather than ClassLoader#getResource(). Therefore the paths may be either relative or absolute.
 */
public final class UriResourceHelper {

    /**
     * Type contains only static members.
     */
    private UriResourceHelper() {
    }

    /**
     * Attempts to resolve the URI of a resource from the curent classpath relative to this class. If not found, an IllegalArgumentException will be
     * thrown.
     */
    public static URI requireUriFromClasspath(final String path) {

        return requireUriFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve the URI of a resource from the curent classpath, relative to the calling class. If not found, an IllegalArgumentException
     * will be thrown.
     */
    public static URI requireUriFromClasspath(final String path, final Class<?> caller) {

        final var value = resolveUriFromClasspath(path, caller);
        return require(path, value);
    }

    /**
     * Attempts to resolve the URI of a resource from the curent classpath, relative to this class.
     */
    public static Optional<URI> resolveUriFromClasspath(final String path) {

        return resolveUriFromClasspath(path, CodeSourceHelper.class);
    }

    /**
     * Attempts to resolve the URI of a resource from the curent classpath, relative to the calling class.
     */
    public static Optional<URI> resolveUriFromClasspath(final String path, final Class<?> caller) {

        return Optional.ofNullable(caller.getResource(path)).map(Fn1.asTry(URL::toURI));
    }

    /**
     * Helper method to ensure a value is present, or an IllegalArgumentException will be thrown.
     */
    public static <T> T require(final String path, final Optional<T> value) {

        return value.orElseThrow(() -> new IllegalArgumentException("Missing resource %s".formatted(path)));
    }
}
