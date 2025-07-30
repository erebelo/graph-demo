/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import com.erebelo.graphdemo.common.fp.Fn0;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Factory to return various Pipe implementations from this package.
 */
public final class Pipes {

    /**
     * Type containts only static members.
     */
    private Pipes() {
    }

    /**
     * Returns a pipe that reads from an InputStream, writes to an OutputStream and operates with byte[] values. <br> The pipe will not close the
     * streams passed into its operations.
     *
     * @return Pipe Pipe to use
     */
    public static Pipe<byte[], InputStream, OutputStream> bytes() {

        return new BytesPipe();
    }

    /**
     * Returns a pipe that reads from an InputStream, writes to an OutputStream and operates with byte[] values. All suppliers of these values will be
     * lazilly evaluated. <br> The pipe will not close the streams passed into its operations.
     *
     * @return Pipe Pipe to use
     */
    public static Pipe<byte[], Fn0<? extends InputStream>, Fn0<? extends OutputStream>> bytesSupplier() {

        return new BytesSupplierPipe();
    }

    /**
     * Returns a pipe that reads from an Reader, writes to an Writer and operates with String values. <br> The pipe will not close the streams passed
     * into its operations.
     *
     * @return Pipe Pipe to use
     */
    public static Pipe<String, Reader, Writer> chars() {

        return new StringPipe();
    }

    /**
     * Returns a pipe that reads from an Reader, writes to an Writer and operates with String values. All suppliers of these values will be lazilly
     * evaluated. <br> The pipe will close the streams passed into its operations.
     *
     * @return Pipe Pipe to use
     */
    public static Pipe<String, Fn0<? extends Reader>, Fn0<? extends Writer>> charsSupplier() {

        return new StringSupplierPipe();
    }

    /**
     * Returns a "reverse" Pipe where data is "read" from an output stream and "written" to an input stream. This is useful in a number of situations,
     * such as where one is producing output on the fly that needs to be read in by another process. <br> Note that this implementation uses
     * PipedInputStream and PipedOutputStream, so an extra thread is created transparently in the background to buffer the output and input. This
     * thread does not need to be separately managed. <br> The pipe will not close the streams passed into its operations.
     *
     * @return Pipe Pipe to use
     */
    public static Pipe<byte[], Consumer<OutputStream>, Function<InputStream, Long>> reverse() {

        return new ReversePipe();
    }
}
