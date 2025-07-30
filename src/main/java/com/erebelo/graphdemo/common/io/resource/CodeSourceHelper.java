/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.io.resource;

import com.erebelo.graphdemo.common.fp.Io;

import java.io.InputStream;
import java.net.URI;

/**
 * Helper class to query the location of where a class has been loaded from (its CodeSource). This can be used to resolve the containing JAR or build
 * folder, as examples. Note that resolution will depend on the calling class passed in, as each may be loaded from different JAR files.
 */
public final class CodeSourceHelper {

    /**
     * Type contains only static members.
     */
    private CodeSourceHelper() {
    }

    /**
     * Returns whether we are running from a JAR file.
     */
    public static boolean isJar(final Class<?> caller) {

        return locateCodeSource(caller).toString().endsWith(".jar");
    }

    /**
     * Returns the location where the specified class was loaded from.
     */
    public static URI locateCodeSource(final Class<?> caller) {

        return Io.withReturn(
                () -> caller.getProtectionDomain().getCodeSource().getLocation().toURI());
    }

    /**
     * Returns an input stream where the sepcified class was loaded from.
     */
    public static InputStream resolveStream(final Class<?> caller) {

        return Io.withReturn(
                () -> caller.getProtectionDomain().getCodeSource().getLocation().openStream());
    }
}
