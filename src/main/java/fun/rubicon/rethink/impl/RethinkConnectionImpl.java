package fun.rubicon.rethink.impl;

import com.rethinkdb.net.Connection;
import fun.rubicon.rethink.RethinkConnection;

import java.util.concurrent.TimeoutException;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RethinkConnectionImpl implements RethinkConnection {

    private boolean isFree;
    private Connection connection;
    private int usages = 0;

    public RethinkConnectionImpl(boolean isFree, Connection connection) {
        this.isFree = isFree;
        this.connection = connection;
    }

    @Override
    public boolean isFree() {
        return isFree;
    }

    @Override
    public void setFree(boolean isFree) {
        this.isFree = isFree;
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public void connect() throws TimeoutException {
        connection.connect();
    }

    @Override
    public int getUsages() {
        return usages;
    }

    @Override
    public void setUsages(int usages) {
        this.usages = usages;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
