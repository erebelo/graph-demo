/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import com.erebelo.graphdemo.common.io.IoConstants;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Input stream that will throw an EOFException once a pre-set maximum number of
 * bytes has been read.
 */
public final class FiniteInputStream extends FilterInputStream {

    /**
     * Total bytes read.
     */
    private long bytesRemaining;

    /**
     * Creates a finite input stream that will only write to the default large
     * object (LOB) length in bytes. Currently, this is set to 64 mB.
     *
     * @param in
     *            Underlying stream to read
     */
    public FiniteInputStream(final InputStream in) {

        this(in, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
    }

    /**
     * Creates a finite input stream that will only read up to maximum bytes.
     *
     * @param in
     *            Underlying stream to read
     * @param maximumBytes
     *            Number of bytes after which reading will cease
     */
    public FiniteInputStream(final InputStream in, final long maximumBytes) {

        super(in);
        bytesRemaining = maximumBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {

        if (bytesRemaining <= 0) {
            throw new EOFException("Maximum number of bytes read");
        }
        final var result = super.read();
        if (result != -1) {
            bytesRemaining--;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte @NotNull [] buffer) throws IOException {

        return read(buffer, 0, buffer.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte @NotNull [] buffer, final int offset, final int length) throws IOException {

        if (bytesRemaining <= 0) {
            throw new EOFException("Maximum number of bytes read");
        }
        final var lengthToRead = (length > bytesRemaining) ? (int) bytesRemaining : length;
        final var bytesRead = super.read(buffer, offset, lengthToRead);
        if (bytesRead > 0) {
            bytesRemaining -= bytesRead;
        }
        return bytesRead;
    }
}
