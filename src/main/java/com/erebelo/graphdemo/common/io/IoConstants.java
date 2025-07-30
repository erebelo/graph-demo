/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io;

/**
 * Constants for IO operations.
 */
public final class IoConstants {

    /**
     * Default buffer length to use for streams. (4 kB)
     */
    public static final int DEFAULT_BUFFER_LENGTH = 1024;

    /**
     * Normal maximum size for a large object. (64 mB)
     */
    public static final int DEFAULT_LOB_BUFFER_LENGTH = 1024 * 1024 * 64;

    /**
     * Mask to convert from a byte to an int.
     */
    public static final int INT_MASK = 0xff;

    /**
     * Zero byte value.
     */
    public static final byte ZERO_BYTE = 0;

    /**
     * Empty byte array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Private constructor. Type contains only constants.
     */
    private IoConstants() {
    }
}
