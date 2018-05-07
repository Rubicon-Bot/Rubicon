package fun.rubicon.rethink;

import fun.rubicon.rethink.impl.RethinkConnectionPoolImpl;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RethinkConnectionPoolBuilder {

    private String hostname = "127.0.0.7";
    private String username = "admin";
    private int port = 28015;
    private String password = "";
    private String db = "test";
    private int maxConnections = 10;

    public RethinkConnectionPoolBuilder hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public RethinkConnectionPoolBuilder username(String username) {
        this.username = username;
        return this;
    }

    public RethinkConnectionPoolBuilder port(int port) {
        this.port = port;
        return this;
    }

    public RethinkConnectionPoolBuilder password(String password) {
        this.password = password;
        return this;
    }

    public RethinkConnectionPoolBuilder db(String db) {
        this.db = db;
        return this;
    }

    public RethinkConnectionPoolBuilder maxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public RethinkConnectionPool build() {
        return new RethinkConnectionPoolImpl(hostname, username, port, password, db, maxConnections);
    }
}
