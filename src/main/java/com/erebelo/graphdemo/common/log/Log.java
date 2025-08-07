/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.common.log;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General purpose logging utility class. This currently delegates out to SLF4J.
 * The methods all accept suppliers, so the messages are lazily evaluated.
 * Therefore, you should not have to worry about a given logging level being
 * enabled (since this type will check that for you). An example usage:
 *
 * <p>
 * {@code LOG.error(Foo.class, () -> "Unable to initialize component"); }
 *
 * <p>
 * Note that logging is typically set up via a Micronaut container or runtime.
 * The configuration for logging is controlled by the logback.xml file normally
 * located in src/main/resourecs (or anywhere that can be resolved at the root
 * of the classpath).
 */
public final class Log {

    /**
     * Private constructor. Type contains only static members.
     */
    private Log() {
    }

    /**
     * Log at the trace level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     */
    public static void trace(final Class<?> caller, final Supplier<String> message) {

        log(caller, Logger::isTraceEnabled, l -> l.trace(message.get()));
    }

    /**
     * Log at the trace level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     * @param t
     *            Exception encountered
     */
    public static void trace(final Class<?> caller, final Supplier<String> message, final Throwable t) {

        log(caller, Logger::isTraceEnabled, l -> l.trace(message.get(), t));
    }

    /**
     * Log at the debug level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     */
    public static void debug(final Class<?> caller, final Supplier<String> message) {

        log(caller, Logger::isDebugEnabled, l -> l.debug(message.get()));
    }

    /**
     * Log at the debug level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     * @param t
     *            Exception encountered
     */
    public static void debug(final Class<?> caller, final Supplier<String> message, final Throwable t) {

        log(caller, Logger::isDebugEnabled, l -> l.debug(message.get(), t));
    }

    /**
     * Log at the info level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     */
    public static void info(final Class<?> caller, final Supplier<String> message) {

        log(caller, Logger::isInfoEnabled, l -> l.info(message.get()));
    }

    /**
     * Log at the info level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     * @param t
     *            Exception encountered
     */
    public static void info(final Class<?> caller, final Supplier<String> message, final Throwable t) {

        log(caller, Logger::isInfoEnabled, l -> l.info(message.get(), t));
    }

    /**
     * Log at the warn level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     */
    public static void warn(final Class<?> caller, final Supplier<String> message) {

        log(caller, Logger::isWarnEnabled, l -> l.warn(message.get()));
    }

    /**
     * Log at the warn level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     * @param t
     *            Exception encountered
     */
    public static void warn(final Class<?> caller, final Supplier<String> message, final Throwable t) {

        log(caller, Logger::isWarnEnabled, l -> l.warn(message.get(), t));
    }

    /**
     * Log at the error level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     */
    public static void error(final Class<?> caller, final Supplier<String> message) {

        log(caller, Logger::isErrorEnabled, l -> l.error(message.get()));
    }

    /**
     * Log at the error level.
     *
     * @param caller
     *            Calling type
     * @param message
     *            Message to output
     * @param t
     *            Exception encountered
     */
    public static void error(final Class<?> caller, final Supplier<String> message, final Throwable t) {

        log(caller, Logger::isErrorEnabled, l -> l.error(message.get(), t));
    }

    /**
     * Helper method for all logging levels that obtains a reference to the proper
     * logger, which is based on the calling class, and ensures that the log should
     * be output based on the logging level specified.
     *
     * @param caller
     *            Calling type
     * @param enabled
     *            Predicate to test if logging is enabled
     * @param fx
     *            Consuming function to call to emit the log message
     */
    private static void log(final Class<?> caller, final Predicate<Logger> enabled, final Consumer<Logger> fx) {

        final var logger = LoggerFactory.getLogger(caller);
        if (enabled.test(logger)) {
            fx.accept(logger);
        }
    }
}
