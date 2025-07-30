/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import com.erebelo.graphdemo.common.fp.Io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Pipe implementation that reads from InputStream, writes to OutputStream and works with byte[] values. <br> This implementation does not close the
 * streams passed in.
 */
final class BytesPipe implements Pipe<byte[], InputStream, OutputStream> {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] read(InputStream in) {

        return Io.withReturn(() -> {
            try (var out = new ByteArrayOutputStream()) {
                go(in, out);
                return out.toByteArray();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] target, OutputStream out) {

        Io.withVoid(() -> {
            try (var byteIn = new ByteArrayInputStream(target)) {
                go(byteIn, out);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("ReassignedVariable")
    public long go(InputStream in, OutputStream out, int bufferSize) {

        return Io.withReturn(() -> {
            var total = 0L;
            final var buffer = new byte[bufferSize];
            var bytesRead = in.read(buffer);
            while (bytesRead >= 0) {
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                    total += bytesRead;
                }
                bytesRead = in.read(buffer);
            }
            out.flush();
            return total;
        });
    }
}
