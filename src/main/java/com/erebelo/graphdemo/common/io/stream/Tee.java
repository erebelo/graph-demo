/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * An output stream that writes to n underlying streams sequentially.
 */
public final class Tee extends OutputStream {

    /**
     * Underlying streams to output to.
     */
    private final Collection<OutputStream> streams;

    /**
     * Creates a tee that will output to the specified streams.
     *
     * @param streams Additional output streams to write to
     */
    public Tee(final OutputStream... streams) {

        this.streams = Collections.unmodifiableList(Arrays.asList(streams));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int b) throws IOException {

        for (final var out : streams) {
            out.write(b);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte @NotNull [] buffer) throws IOException {

        for (final var out : streams) {
            out.write(buffer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte @NotNull [] buffer, final int offset, final int length) throws IOException {

        for (final var out : streams) {
            out.write(buffer, offset, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {

        for (final var out : streams) {
            out.flush();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {

        final var fatal = new IOException("Error closing one or more streams");
        for (final var out : streams) {
            closeInternal(out, fatal);
        }
        if (fatal.getSuppressed().length > 0) {
            throw fatal;
        }
    }

    /**
     * Helper method to safely close a stream and ensure that remaining streams can still be closed.
     *
     * @param target Stream to close
     * @param ex     Exception to eventually throw if any errors exist
     */
    private static void closeInternal(final Closeable target, final Exception ex) {

        try {
            target.close();
        } catch (final IOException e) {
            ex.addSuppressed(e);
        }
    }
}
