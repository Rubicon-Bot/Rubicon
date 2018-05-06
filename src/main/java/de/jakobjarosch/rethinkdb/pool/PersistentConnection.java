package de.jakobjarosch.rethinkdb.pool;


import com.rethinkdb.ast.ReqlAst;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Connection;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class PersistentConnection extends Connection {

    @FunctionalInterface
    public interface CloseCallback {
        void free();
    }

    private final Connection wrapped;
    private final CloseCallback closeCallback;
    private boolean closed = false;

    public PersistentConnection(Connection wrapped) {
        this(wrapped, () -> {
        });
    }

    public PersistentConnection(Connection wrapped, CloseCallback closeCallback) {
        super(new Builder());
        this.wrapped = wrapped;
        this.closeCallback = closeCallback;
    }

    @Override
    public void close() {
        this.closed = true;
        closeCallback.free();
    }

    @Override
    public void close(boolean shouldNoreplyWait) {
        this.closed = true;
        closeCallback.free();
    }

    private void checkOpen() {
        if (closed) {
            throw new ReqlDriverError("Connection already returned to pool.");
        }
    }

    @Override
    public Optional<String> db() {
        checkOpen();
        return wrapped.db();
    }

    @Override
    public void connect() throws TimeoutException {
        checkOpen();
        throw new ReqlDriverError("Connect is not supported on an already connected pool connection.");
    }

    @Override
    public Connection reconnect() {
        throw new ReqlDriverError("Reconnect not supported, try to get a new connection from pool.");
    }

    @Override
    public Connection reconnect(boolean noreplyWait, Optional<Long> timeout) throws TimeoutException {
        throw new ReqlDriverError("Reconnect not supported, try to get a new connection from pool.");
    }

    @Override
    public boolean isOpen() {
        return !closed && wrapped.isOpen();
    }

    @Override
    public void use(String db) {
        throw new ReqlDriverError("Switching database is not supported on a pool connection.");
    }

    @Override
    public Optional<Long> timeout() {
        return wrapped.timeout();
    }

    @Override
    public void noreplyWait() {
        checkOpen();
        wrapped.noreplyWait();
    }

    @Override
    public <T, P> T run(ReqlAst term, OptArgs globalOpts, Optional<Class<P>> pojoClass) {
        checkOpen();
        return wrapped.run(term, globalOpts, pojoClass);
    }

    @Override
    public <T, P> T run(ReqlAst term, OptArgs globalOpts, Optional<Class<P>> pojoClass, Optional<Long> timeout) {
        checkOpen();
        return wrapped.run(term, globalOpts, pojoClass, timeout);
    }

    @Override
    public void runNoReply(ReqlAst term, OptArgs globalOpts) {
        checkOpen();
        wrapped.runNoReply(term, globalOpts);
    }
}
