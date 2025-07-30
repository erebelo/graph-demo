/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Input stream that will throw an EOFException once a pre-set maximum number of bytes has been read.
 */
public final class FiniteOutputStream extends FilterOutputStream {

    /**
     * Total bytes read.
     */
    private long bytesRemaining;

    /**
     * Creates a finite input stream that will only read up to maximum bytes.
     *
     * @param out          Output stream to write
     * @param maximumBytes Maximum bytes before output will cease
     */
    public FiniteOutputStream(final OutputStream out, final long maximumBytes) {

        super(out);
        bytesRemaining = maximumBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int value) throws IOException {

        if (bytesRemaining <= 0) {
            throw new EOFException("Maximum number of bytes written");
        }
        super.write(value);
        bytesRemaining--;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte @NotNull [] buffer) throws IOException {

        write(buffer, 0, buffer.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte @NotNull [] buffer, final int offset, final int length) throws IOException {

        if (bytesRemaining <= 0) {
            throw new EOFException("Maximum number of bytes written");
        }
        final var lengthToWrite = (length > bytesRemaining) ? (int) bytesRemaining : length;
        super.write(buffer, offset, lengthToWrite);
        bytesRemaining -= lengthToWrite;
    }
}
