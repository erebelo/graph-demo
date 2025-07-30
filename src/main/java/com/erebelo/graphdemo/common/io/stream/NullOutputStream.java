/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import java.io.OutputStream;

/**
 * Output stream that performs no action but simply consumes any write requests.
 */
public final class NullOutputStream extends OutputStream {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int b) {
    }
}
