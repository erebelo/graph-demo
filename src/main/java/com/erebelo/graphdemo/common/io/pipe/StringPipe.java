/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import com.erebelo.graphdemo.common.fp.Io;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Pipe implementation that reads from Reader, writes to Writer and works with
 * String values. <br>
 * This implementation does not close the streams passed in.
 */
final class StringPipe implements Pipe<String, Reader, Writer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String read(Reader reader) {

        return Io.withReturn(() -> {
            try (var writer = new StringWriter()) {
                go(reader, writer);
                return writer.toString();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String target, Writer writer) {

        Io.withVoid(() -> {
            try (var reader = new StringReader(target)) {
                go(reader, writer);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("ReassignedVariable")
    public long go(Reader reader, Writer writer, int bufferSize) {

        return Io.withReturn(() -> {
            var total = 0L;
            final var buffer = new char[bufferSize];
            var bytesRead = reader.read(buffer);
            while (bytesRead >= 0) {
                if (bytesRead > 0) {
                    writer.write(buffer, 0, bytesRead);
                    total += bytesRead;
                }
                bytesRead = reader.read(buffer);
            }
            writer.flush();
            return total;
        });
    }
}
