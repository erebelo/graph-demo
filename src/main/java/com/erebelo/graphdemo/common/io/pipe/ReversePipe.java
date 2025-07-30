/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Returns a "reverse" Pipe where data is "read" from an output stream and "written" to an input stream. This is useful in a number of situations,
 * such as where one is producing output on the fly that needs to be read in by another process. <br> Note that this implementation uses
 * PipedInputStream and PipedOutputStream, so an extra thread is created transparently in the background to buffer the output and input. This thread
 * does not need to be separately managed. <br> The pipe will not close the streams passed into its operations.
 */
final class ReversePipe implements Pipe<byte[], Consumer<OutputStream>, Function<InputStream, Long>> {

    /**
     * Output stream to "read" from.
     */
    private final PipedOutputStream pipedOut = new PipedOutputStream();

    /**
     * Input stream to "write to.
     */
    private final PipedInputStream pipedIn = new PipedInputStream();

    /**
     * Delegate to use for operations.
     */
    private static final Pipe<byte[], InputStream, OutputStream> delegate = new BytesPipe();

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] read(Consumer<OutputStream> in) {

        return Io.withReturn(() -> {
            try (var byteOut = new ByteArrayOutputStream()) {
                go(in, i -> delegate.go(i, byteOut));
                return byteOut.toByteArray();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] value, Function<InputStream, Long> out) {

        go(o -> delegate.write(value, o), out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long go(Consumer<OutputStream> in, Function<InputStream, Long> out, int bufferSize) {

        final var writer = new Thread(() -> in.accept(pipedOut));
        try {
            Io.withVoid(() -> pipedIn.connect(pipedOut));
            writer.start();
            return out.apply(pipedIn);
        } finally {
            Io.withVoid(writer::join, e -> Log.error(getClass(), () -> "Unable to join reverse pipe writer thread", e));
        }
    }
}
