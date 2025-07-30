/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.stream;

import java.io.InputStream;

/**
 * An input stream that reads nothing and always indicates more data is present.
 */
public final class NullInputStream extends InputStream {

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {

        return 0;
    }
}
