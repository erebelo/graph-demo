/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.pipe;

import static com.erebelo.graphdemo.common.io.IoConstants.DEFAULT_BUFFER_LENGTH;

/**
 * Encapsualtes an InputStrem and an OutputStream and allows all the data from
 * the former to be piped to the latter efficiently and safely. This class will
 * not automatically close the underlying streams. The caller may invoke close()
 * on this type to do so. <br>
 * However, if the argument is a Supplier, then the stream(s) will be included
 * in a try-with-resources block and hence fully closed.
 */
public interface Pipe<R, I, O> {

    /**
     * Reads all data from the specified intput stream into a return value. The
     * stream passed in should be a finite stream to limit possible memory scaling
     * issues.
     *
     * @param in
     *            Stream to read
     * @return R Value of all bytes read
     */
    R read(I in);

    /**
     * Writes all of the specified value to the indicated output stream.
     *
     * @param value
     *            Value to write
     * @param out
     *            Stream to receive output
     */
    void write(R value, O out);

    /**
     * Reads from the specified input and writes to the specified output, returning
     * the total number of bytes or characters processed. This version uses a
     * default buffer size.
     *
     * @param in
     *            Input to read
     * @param out
     *            Output to write
     * @return long Number of bytes or chars piped
     */
    default long go(final I in, final O out) {

        return go(in, out, DEFAULT_BUFFER_LENGTH);
    }

    /**
     * Reads all available data from the input stream and pipes the results to the
     * output stream, flusing the latter at the end of processing. The long returned
     * indicates the total number of bytes processed or piped. See implementations
     * for whether stream(s) are closed. Note that the term "stream" is an
     * encompassing one, including both InputStream and OutputStream, as well as
     * Reader and Writer, or variations of the above.
     *
     * @param in
     *            Input stream to read
     * @param out
     *            Output stream to write
     * @param bufferSize
     *            Buffer size to use
     * @return long Total number of bytes piped
     */
    long go(I in, O out, int bufferSize);
}
