/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.event;

/**
 * Interface whose implementations listen for a specific type of event.
 *
 * @param <T> Type that listener will receive
 */
@FunctionalInterface
public interface Listener<T> {

    /**
     * Notify the listener that an event has occurred.
     *
     * @param event Event to process
     * @return boolean True if handled, false if not
     */
    boolean notify(T event);
}
