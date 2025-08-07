package com.erebelo.graphdemo.common.persist;

import com.erebelo.graphdemo.common.error.IoException;
import com.erebelo.graphdemo.common.fp.Fn0;

/**
 * Convenience operations for using a session.
 */
public final class SessionExecutor {

    /**
     * Type contains only static methods.
     */
    private SessionExecutor() {
    }

    /**
     * Creates a session, executes the operation (commits if succsesful, rollsback
     * if failed) and closes the session.
     */
    @SuppressWarnings("OverlyNestedMethod")
    public static <T> T execute(final SessionFactory factory, final Fn0<T> operation) {

        Session session = null;
        IoException ex = null;
        try {
            session = factory.create();
            final var value = operation.get();
            session.commit();
            return value;
        } catch (final Exception e) {
            ex = new IoException("Operation failed", e);
            if (session != null) {
                try {
                    session.rollback();
                } catch (final Exception rollbackEx) {
                    ex.addSuppressed(new IoException("Operation failed, and session rollback() failed", rollbackEx));
                }
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (final Exception closeEx) {
                    if (ex == null) {
                        ex = new IoException("Operation succeeded, but session close() failed", closeEx);
                    } else {
                        ex.addSuppressed(closeEx);
                    }
                }
            }
        }
        throw ex;
    }
}
