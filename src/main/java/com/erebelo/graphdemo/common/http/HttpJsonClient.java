/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.http;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.io.pipe.Pipes;
import com.erebelo.graphdemo.common.log.Log;
import com.erebelo.graphdemo.common.serde.JsonSerde;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * HTTP client to facilitate working with REST calls using JSON. The Gson
 * library is used for serde.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public final class HttpJsonClient {

    /**
     * Thread-safe HTTP client builder.
     */
    private static final HttpClient.Builder HTTP_BUILDER = HttpClient.newBuilder();

    /**
     * Type contains only static members.
     */
    private HttpJsonClient() {
    }

    /**
     * Execute a HTTP GET and expect no HTTP response body.
     */
    public static int getNoReply(final URI uri, final Map<String, String> headers) {

        final HttpResponse<?> response = execute(() -> toGetRequest(uri, headers));
        return response.statusCode();
    }

    /**
     * Execute a HTTP GET with a JSON payload and expect no HTTP response body.
     */
    public static int getFormUrlEncodedNoReply(final URI uri, final Map<String, String> headers,
            final Map<String, String> params) {

        final HttpResponse<?> response = execute(() -> toGetFormUrlEncoded(uri, headers, params));
        return response.statusCode();
    }

    /**
     * Execute a HTTP POST with a JSON payload and expect no HTTP response body.
     */
    public static int postFormUrlEncodedNoReply(final URI uri, final Map<String, String> headers,
            final Map<String, String> params) {

        final HttpResponse<?> response = execute(() -> toPostFormUrlEncoded(uri, headers, params));
        return response.statusCode();
    }

    /**
     * Execute a HTTP POST with a JSON payload and expect no HTTP response body.
     */
    public static <R> int postJsonNoReply(final URI uri, final Map<String, String> headers, final R requestBody) {

        final HttpResponse<?> response = execute(() -> toJsonRequest(uri, headers, requestBody));
        return response.statusCode();
    }

    /**
     * Execute a HTTP GET and expect a JSON response in the HTTP reply.
     */
    public static <T> T getUrlEncodedWithReply(final URI uri, final Map<String, String> headers,
            final Map<String, String> params, final Class<T> target) {

        return executeWithReply(() -> toGetFormUrlEncoded(uri, headers, params), target);
    }

    /**
     * Execute a HTTP GET and expect a JSON response in the HTTP reply.
     */
    public static <T> T getWithReply(final URI uri, final Map<String, String> headers, final Class<T> target) {

        return executeWithReply(() -> toGetRequest(uri, headers), target);
    }

    /**
     * Execute a HTTP POST with a JSON payload and expect a JSON response in the
     * HTTP reply.
     */
    public static <T> T postFormUrlEncodedWithReply(final URI uri, final Map<String, String> headers,
            final Map<String, String> params, final Class<T> target) {

        return executeWithReply(() -> toPostFormUrlEncoded(uri, headers, params), target);
    }

    /**
     * Execute a HTTP POST with a JSON payload and expect a JSON response in the
     * HTTP reply.
     */
    public static <R, T> T postJsonWithReply(final URI uri, final Map<String, String> headers, final R requestBody,
            final Class<T> target) {

        return executeWithReply(() -> toJsonRequest(uri, headers, requestBody), target);
    }

    /**
     * Internal method to consistently handle requests that have JSON responses.
     */
    @SuppressWarnings({"unchecked", "ChainOfInstanceofChecks"})
    private static <T> T executeWithReply(final Supplier<HttpRequest> fx, final Class<T> target) {

        final var response = execute(fx);
        if (target == String.class) {
            return (T) Pipes.charsSupplier().read(() -> new InputStreamReader(response.body(), StandardCharsets.UTF_8));
        }
        if (target == byte[].class) {
            return (T) Pipes.bytesSupplier().read(response::body);
        }
        if (target == InputStream.class) {
            return (T) response.body();
        }
        return Io.withReturn(() -> {
            try (var reader = HttpResponseCharset.getReader(response)) {
                return JsonSerde.fromJson(reader, target);
            }
        });
    }

    /**
     * Internal method to consistently execute a request and then validate and
     * return the response.
     */
    private static HttpResponse<InputStream> execute(final Supplier<HttpRequest> fx) {

        final var request = fx.get();
        try (var client = HTTP_BUILDER.build()) {
            final var response = Io.withReturn(() -> client.send(request, HttpResponse.BodyHandlers.ofInputStream()));
            final var code = response.statusCode();
            if ((code < HttpURLConnection.HTTP_OK) || (code >= HttpURLConnection.HTTP_MULT_CHOICE)) {
                final var content = Pipes.charsSupplier().read(() -> HttpResponseCharset.getReader(response));
                throw new HttpException(code, content);
            }
            return response;
        }
    }

    /**
     * Consitently creates a HTTP GET request from a URI.
     */
    private static HttpRequest toGetRequest(final URI uri, final Map<String, String> headers) {

        final var builder = HttpRequest.newBuilder().uri(uri);
        populateHeaders(headers, builder);
        final var request = builder.build();
        Log.debug(HttpJsonClient.class,
                () -> "HTTP request sent: %n%s%n".formatted(HttpPrettyPrinter.toString(request, Optional.empty())));
        return request;
    }

    /**
     * Consitently creates a HTTP GET request from a URI with a form-urlencoded
     * payload.
     */
    private static HttpRequest toGetFormUrlEncoded(final URI uri, final Map<String, String> headers,
            final Map<String, String> params) {

        final var queryString = toUrlEncoded(params);
        return toGetRequest(URI.create("%s?%s".formatted(uri.toString(), queryString)), headers);
    }

    /**
     * Consitently creates a HTTP POST request from a URI with a form-urlencoded
     * payload.
     */
    private static HttpRequest toPostFormUrlEncoded(final URI uri, final Map<String, String> headers,
            final Map<String, String> params) {

        final var body = toUrlEncoded(params);
        return toPostRequest(uri, headers, "application/x-www-form-urlencoded", body);
    }

    /**
     * Helper method to transform a set of key/value pairs into a url-encoded
     * string.
     */
    private static String toUrlEncoded(final Map<String, String> params) {

        return params.entrySet().stream()
                .map(e -> "%s=%s".formatted(e.getKey(), URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));
    }

    /**
     * Consitently creates a HTTP POST request from a URI with a JSON payload.
     */
    private static <R> HttpRequest toJsonRequest(final URI uri, final Map<String, String> headers,
            final R requestBody) {

        final var json = JsonSerde.toJson(requestBody);
        return toPostRequest(uri, headers, "application/json", json);
    }

    /**
     * Helper method to consistently construct HTTP POST request.
     */
    private static HttpRequest toPostRequest(final URI uri, final Map<String, String> headers, final String contentType,
            final String body) {

        final var builder = HttpRequest.newBuilder().uri(uri)
                .header("Content-type", "%s; charset=UTF-8".formatted(contentType)).POST(BodyPublishers.ofString(body));
        populateHeaders(headers, builder);
        final var request = builder.build();
        Log.debug(HttpJsonClient.class,
                () -> "HTTP request sent: %n%s%n".formatted(HttpPrettyPrinter.toString(request, Optional.of(body))));
        return request;
    }

    /**
     * Ensures that headers are properly encoded and consistently populated.
     */
    private static void populateHeaders(final Map<String, String> headers, final HttpRequest.Builder builder) {

        headers.forEach((k, v) -> builder.header(k, URLEncoder.encode(v, StandardCharsets.UTF_8)));
    }
}
