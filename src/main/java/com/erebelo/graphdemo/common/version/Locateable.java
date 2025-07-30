package com.erebelo.graphdemo.common.version;

/**
 * Indicates a type is defined by its Locator (NanoID and version).
 */
@FunctionalInterface
public interface Locateable {

    /**
     * Returns the unique locator for this Locateable item.
     */
    Locator locator();

    /**
     * Compares two identifiable items for equality based on their locators.
     */
    static boolean equals(final Locateable source, final Locateable target) {

        return source.locator().equals(target.locator());
    }

    /**
     * Computes hash code for an identifiable item based on its locator.
     */
    static int hashCode(final Locateable target) {

        return target.locator().hashCode();
    }
}
