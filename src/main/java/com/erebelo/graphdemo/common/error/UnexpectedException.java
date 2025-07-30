/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

import java.io.Serial;

/**
 * Exception indicating that we encountered a believed-to-be impossible situations or fundamentally represent a programming error, violation of an
 * invariant, corner case not anticipated, etc. <br> Examples of these kinds of situation are NullPointerException, ArrayIndexOutofBoundsException,
 * ParseException, URISyntaxException, etc. <br> This is in contrast to operations that can and will fail as a matter of course. Any operation that
 * performs IO or has a side-effect falls into this category. Examples of these tyupes of issue are: IOException, SocketException, SQLException, etc.
 * Any can fail at any time even though the actual code is totally valid and will work normally in most situations. <br> Whenever you have a case
 * where IO or side-effects occur, use {@code IoException}. For other cases, default to using {@code UnexpectedException}.
 */
public final class UnexpectedException extends RuntimeException {

    /**
     * Serialization constant.
     */
    @Serial
    private static final long serialVersionUID = 1554475580689944097L;

    /**
     * Creates an exception with the specified message.
     *
     * @param message Message to include
     */
    public UnexpectedException(final String message) {

        super(message);
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param message Message to include
     * @param cause   Original exception received
     */
    public UnexpectedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param cause Original exception received
     */
    public UnexpectedException(final Throwable cause) {

        super(cause);
    }
}
