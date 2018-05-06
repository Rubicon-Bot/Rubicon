package de.jakobjarosch.rethinkdb.pool;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import javax.inject.Inject;

public class ConnectionFactory extends BasePooledObjectFactory<Connection> {


    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    @Inject
    public ConnectionFactory(String hostname, int port, String username, String password, String database) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public Connection create() {
        return RethinkDB.r.connection()
                .hostname(hostname)
                .port(port)
                .user(username, password)
                .db(database)
                .connect();
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<Connection> connection) throws Exception {
        connection.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Connection> connection) {
        return connection.getObject().isOpen();
    }
}
