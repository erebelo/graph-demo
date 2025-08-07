/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;

/**
 * Helper stream that will output the data read to a separate file for analysis,
 * logging or debugging.
 */
public final class LoggingInputStream extends FilterInputStream {

    /**
     * File output stream to write to.
     */
    private final FileOutputStream fileOut;

    /**
     * Creates a logging stream that will output all bytes read to the specified URI
     * for later analysis.
     *
     * @param uri
     *            URI to output results to
     * @param in
     *            Input stream to read
     * @throws IOException
     *             Error opening a file at the specified URI
     */
    public LoggingInputStream(final String uri, final InputStream in) throws IOException {

        super(in);
        fileOut = new FileOutputStream(uri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {

        final var result = super.read();
        if (result > -1) {
            fileOut.write(result);
            fileOut.flush();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte @NotNull [] buffer) throws IOException {

        final var result = super.read(buffer);
        if (result > -1) {
            fileOut.write(buffer, 0, result);
            fileOut.flush();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte @NotNull [] buffer, final int offset, final int length) throws IOException {

        final var result = super.read(buffer, offset, length);
        if (result > -1) {
            fileOut.write(buffer, offset, result);
            fileOut.flush();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {

        super.close();
        fileOut.flush();
        fileOut.close();
    }
}
