/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.http;

import java.io.Serial;

/**
 * Exception thrown when the HTTP status code received is outside the 200
 * series.
 */
public final class HttpException extends RuntimeException {

    /**
     * Serialization constant.
     */
    @Serial
    private static final long serialVersionUID = -6927773739313371398L;

    /**
     * HTTP status code returned.
     */
    private final int statusCode;

    /**
     * HTTP response body content.
     */
    private final String body;

    /**
     * Creates an exception.
     *
     * @param statusCode
     *            HTTP status code
     * @param body
     *            HTTP response body
     */
    public HttpException(final int statusCode, final String body) {

        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Returns the HTTP status code received in the response.
     *
     * @return int HTTP status code
     */
    public int getStatusCode() {

        return statusCode;
    }

    /**
     * Returns the body received in the HTTP response.
     *
     * @return String HTTP body (will not be null but may be empty)
     */
    public String getBody() {

        return body;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {

        return "%d - %s".formatted(statusCode, body);
    }
}
