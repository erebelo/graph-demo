/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.error;

import java.io.Serial;

/**
 * Exception indicating that we encountered during an IO operation or anything
 * that has a side-effect. Within functional programming, pure functions do not
 * perform IO or have side-effects. However, for a system to interact with the
 * real world or perform work, side-effects are required. <br>
 * Examples of side effects include (but are not limited to): <br>
 *
 * <ul>
 * <li>IOException - error interacting with input or output streams
 * <li>SQLException - error interacting with the database
 * <li>SocketException - error interacting with the networking interface
 * </ul>
 *
 * <br>
 * However, a large population of errors that can occur at runtime do not
 * necessarily involve side effects. A few examples: <br>
 *
 * <ul>
 * <li>NullPointerException
 * <li>ArrayIndexOutOfBoundsException
 * <li>ParseException
 * <li>URISyntaxException
 * </ul>
 * <p>
 * The general best practice is to throw a {@code IoException} for true IO or
 * operations that perform side-effects. For the other cases that can occur,
 * throw a {@code UnexpectedException}.
 */
public final class IoException extends RuntimeException {

    /**
     * Serialization constant.
     */
    @Serial
    private static final long serialVersionUID = 1554475580689944097L;

    /**
     * Creates an exception with the specified message.
     *
     * @param message
     *            Message to include
     */
    public IoException(final String message) {

        super(message);
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param message
     *            Message to include
     * @param cause
     *            Original exception received
     */
    public IoException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates an exception with the specified cause.
     *
     * @param cause
     *            Original exception received
     */
    public IoException(final Throwable cause) {

        super(cause);
    }
}
