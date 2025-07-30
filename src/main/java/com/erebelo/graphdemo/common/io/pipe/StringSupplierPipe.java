/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import com.erebelo.graphdemo.common.fp.Fn0;
import com.erebelo.graphdemo.common.fp.Io;

import java.io.Reader;
import java.io.Writer;

/**
 * Pipe implementation that reads from Reader, writes to Writer and works with String values. All suppliers are lazily evaluated. <br> This
 * implementation will close the streams passed in.
 */
final class StringSupplierPipe implements Pipe<String, Fn0<? extends Reader>, Fn0<? extends Writer>> {

    /**
     * Delegate for character-based operations.
     */
    private static final StringPipe delegate = new StringPipe();

    /**
     * {@inheritDoc}
     */
    @Override
    public String read(Fn0<? extends Reader> reader) {

        return Io.withReturn(() -> {
            try (var stream = reader.get()) {
                return delegate.read(stream);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String target, Fn0<? extends Writer> writer) {

        Io.withVoid(() -> {
            try (var stream = writer.get()) {
                delegate.write(target, stream);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long go(Fn0<? extends Reader> reader, Fn0<? extends Writer> writer, int bufferSize) {

        return Io.withReturn(() -> {
            try (var r = reader.get();
                 var w = writer.get()) {
                return delegate.go(r, w, bufferSize);
            }
        });
    }
}
