/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.http;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.log.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Helper class to deal with the charset of a HTTP response.
 */
final class HttpResponseCharset {

    /**
     * Default charset.
     */
    private static final Charset DEFAULT = StandardCharsets.UTF_8;

    /**
     * HTTP header containing charset.
     */
    private static final String HEADER = "Content-Type";

    /**
     * Token within header speecifying charset.
     */
    private static final String TOKEN = "charset=";

    /**
     * Delimiter between header tokens.
     */
    private static final String DELIMITER = ";";

    /**
     * Type contains only static members.
     */
    private HttpResponseCharset() {
    }

    /**
     * Parses the HTTP response to determine the appropriate character set to use from the Content-Type header.
     */
    public static Charset parse(final HttpResponse<?> response) {

        return response.headers()
                .firstValue(HEADER)
                .map(HttpResponseCharset::parseHeader)
                .orElse(DEFAULT);
    }

    /**
     * Returns a reader with the appropriate character set from the supplied response.
     */
    public static Reader getReader(final HttpResponse<InputStream> response) {

        return new InputStreamReader(response.body(), parse(response));
    }

    /**
     * Helper method to parse the header.
     */
    private static Charset parseHeader(final String contentType) {

        return Arrays.stream(contentType.split(DELIMITER))
                .map(v -> v.toLowerCase().trim())
                .filter(v -> v.startsWith(TOKEN))
                .findFirst()
                .map(v -> v.substring(TOKEN.length()))
                .map(v -> Io.withReturn(() -> Charset.forName(v), e -> {
                    Log.error(HttpResponseCharset.class, () -> "Invalid charset %s".formatted(v), e);
                    return DEFAULT;
                }))
                .orElse(DEFAULT);
    }
}
