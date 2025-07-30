package com.erebelo.graphdemo.common.version;

import com.erebelo.graphdemo.common.annotation.Stable;

@Stable
public record Locator(NanoId id, int version) {

    private static final int FIRST_VERSION = 1;

    public static Locator generate() {

        return new Locator(NanoId.generate(), FIRST_VERSION);
    }

    public static Locator first(final NanoId id) {

        return new Locator(id, FIRST_VERSION);
    }

    public Locator increment() {

        return new Locator(id, version + 1);
    }
}
