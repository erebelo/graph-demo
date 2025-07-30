package com.erebelo.graphdemo.model;

import com.erebelo.graphdemo.common.fp.Fn1;
import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.common.version.Locateable;
import com.erebelo.graphdemo.common.version.Locator;

/**
 * Encapsulates a reference in the graph that can exist either in a fully loaded or unloaded state. When unloaded, a locator will exist that can be
 * used to find the record to fully load it.
 */
@SuppressWarnings({"MarkerInterface", "unused"})
public sealed interface Reference<T extends Locateable> extends Locateable
        permits Reference.Loaded, Reference.Unloaded {

    /**
     * Represents a graph reference that has been fully loaded.
     */
    record Loaded<T extends Locateable>(T value) implements Reference<T> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Locator locator() {

            return value.locator();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object target) {

            return (target instanceof final Reference<?> ref) && ref.locator().equals(value.locator());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {

            return value.locator().hashCode();
        }
    }

    /**
     * Represents a graph reference that only has a locator and is not fully loaded.
     */
    record Unloaded<T extends Locateable>(Locator locator, Class<T> type) implements Reference<T> {

        /**
         * Load this graph reference via its locator using the supplied loader function.
         */
        public Loaded<T> load(final Fn1<Locator, T> loader) {

            final var loaded = Io.withReturn(() -> loader.apply(locator));
            return new Loaded<>(loaded);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object target) {

            return (target instanceof final Reference<?> ref) && ref.locator().equals(locator);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {

            return locator.hashCode();
        }
    }
}
