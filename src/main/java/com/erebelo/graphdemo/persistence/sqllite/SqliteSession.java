/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package com.erebelo.graphdemo.persistence.sqllite;

import com.erebelo.graphdemo.common.fp.Io;
import com.erebelo.graphdemo.persistence.Session;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

/**
 * SQLite implementation of Session with transaction support.
 */
public final class SqliteSession implements Session, SqliteHandleProvider {

    private final Handle handle;

    public SqliteSession(final Jdbi jdbi) {

        handle = jdbi.open();
        handle.begin();
    }

    @Override
    public void commit() {

        Io.withVoid(handle::commit);
    }

    @Override
    public void rollback() {

        Io.withVoid(handle::rollback);
    }

    @Override
    public void close() {

        Io.withVoid(() -> {
            // JDBI requires transactions to be explicitly committed or rolled back
            // before closing. If transaction is still open, roll it back.
            if (handle.isInTransaction()) {
                handle.rollback();
            }
            handle.close();
        });
    }

    @Override
    public Handle handle() {

        return handle;
    }
}
