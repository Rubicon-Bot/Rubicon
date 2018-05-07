package de.jakobjarosch.rethinkdb.pool;


import com.rethinkdb.gen.exc.ReqlUserError;
import com.rethinkdb.net.Connection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RethinkDBPoolBuilder {

    private String hostname = "127.0.0.1";
    private int port = 28015;
    private String username = "admin";
    private String password = "";
    private String database = "test";

    private int maxConnections = 10;
    private int minFreeConnections = 1;
    private int maxFreeConnections = 5;
    private int timeout = 60;

    private GenericObjectPoolConfig config;

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder port(int port) {
        if (port < 1 || port > 65535)
            throw new ReqlUserError("Constraint violated: 1 <= port <= 65535");
        this.port = port;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder username(String username) {
        this.username = username;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder password(String password) {
        this.password = password;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder database(String database) {
        this.database = database;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder maxConnections(int maxConnections) {
        checkConnectionConstraints(maxConnections, this.minFreeConnections, this.maxFreeConnections);
        this.maxConnections = maxConnections;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder minFreeConnections(int minFreeConnections) {
        checkConnectionConstraints(this.maxConnections, minFreeConnections, this.maxFreeConnections);
        this.minFreeConnections = minFreeConnections;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder maxFreeConnections(int maxFreeConnections) {
        checkConnectionConstraints(this.maxConnections, this.minFreeConnections, maxFreeConnections);
        this.maxFreeConnections = maxFreeConnections;
        return this;
    }

    /**
     * When setting a custom config the basic configuration is ignored and the values of this config is used instead.
     *
     * @param config The config which should be applied to the pool.
     * @return Returns the builder
     */
    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder customConfig(GenericObjectPoolConfig config) {
        this.config = config;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPoolBuilder timeout(int timeout) {
        if (timeout < 1)
            throw new ReqlUserError("Timeout must be at least 1 second");
        this.timeout = timeout;
        return this;
    }

    @SuppressWarnings("unused")
    public RethinkDBPool build() {
        ConnectionFactory factory = new ConnectionFactory(hostname, port, username, password, database);


        GenericObjectPoolConfig config = this.config;
        if (config == null) {
            config = new GenericObjectPoolConfig();
            config.setMaxTotal(maxConnections);
            config.setMinIdle(minFreeConnections);
            config.setMaxIdle(maxFreeConnections);
        }

        GenericObjectPool<Connection> pool = new GenericObjectPool<>(factory, config);
        return new RethinkDBPool(pool, timeout);
    }

    private void checkConnectionConstraints(int maxConnections, int minFreeConnections, int maxFreeConnections) {
        if (maxConnections < minFreeConnections)
            throw new ReqlUserError("Constraint violated: maxConnections >= minFreeConnections.");
        if (maxConnections < maxFreeConnections)
            throw new ReqlUserError("Constraint violated: maxConnections >= maxFreeConnections");
        if (maxFreeConnections < minFreeConnections)
            throw new ReqlUserError("Constraint violated: maxFreeConnections >= minFreeConnections");
    }
}
