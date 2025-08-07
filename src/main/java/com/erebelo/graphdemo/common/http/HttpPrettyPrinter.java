/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.http;

import com.erebelo.graphdemo.common.error.UnexpectedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.util.Optional;

/**
 * Formats the HTTP request in a log-friendly format.
 */
final class HttpPrettyPrinter {

    /**
     * Type contains only static members.
     */
    private HttpPrettyPrinter() {
    }

    /**
     * Formats the HTTP request and body. Note this may divulge privacy/security
     * details if output in a log.
     */
    public static String toString(final HttpRequest request, final Optional<String> body) {

        try (var out = new StringWriter(); var writer = new PrintWriter(out)) {
            writer.println(
                    "%s %s %s".formatted(request.method(), request.uri(), request.version().orElse(Version.HTTP_1_1)));
            for (final var entry : request.headers().map().entrySet()) {
                for (final var value : entry.getValue()) {
                    writer.println("%s: %s".formatted(entry.getKey(), value));
                }
            }
            body.ifPresent(writer::println);
            return out.toString().trim();
        } catch (final IOException notPossible) {
            throw new UnexpectedException("Unexpected error writing to string", notPossible);
        }
    }
}
