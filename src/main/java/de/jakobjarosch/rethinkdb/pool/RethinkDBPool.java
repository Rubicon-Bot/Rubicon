package de.jakobjarosch.rethinkdb.pool;


import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.net.Connection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RethinkDBPool} is able to serve RethinkDB connection in a multi-threaded environment.
 * Connections can be retrieved by calling {@link #getConnection()}.
 * When a connection should be returned to the pool, it is enough to call {@link Connection#close()},
 * or use a try-resource scope.
 */
public class RethinkDBPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RethinkDBPool.class);

    private final GenericObjectPool<Connection> pool;
    private final int defaultTimeout;

    private final ConnectionStats stats = new ConnectionStats();

    RethinkDBPool(GenericObjectPool<Connection> pool,
                  int defaultTimeout) {
        this.pool = pool;
        this.defaultTimeout = defaultTimeout;
    }

    @SuppressWarnings("unused")
    public void shutdown() {
        pool.close();
        pool.setTimeBetweenEvictionRunsMillis(-1);
    }

    /**
     * @return Returns a free connection within the default timeout.
     * @throws ReqlDriverError Throws error when no free connection is available within the default timeout.
     */
    @SuppressWarnings("unused")
    public Connection getConnection() {
        return getConnection(defaultTimeout);
    }

    /**
     * @param timeout Timeout in seconds.
     * @return Returns a free connection within the specified timeout.
     * @throws ReqlDriverError Throws error when no free connection is available within specified timeout.
     */
    @SuppressWarnings("unused")
    public Connection getConnection(int timeout) {
        if (pool.isClosed()) {
            throw new ReqlDriverError("Pool is not started.");
        }

        try {
            final long startRetrieve = System.currentTimeMillis();
            final Connection connection = pool.borrowObject(timeout * 1000);
            stats.add(System.currentTimeMillis() - startRetrieve);
            return new PersistentConnection(connection);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve connection from pool.", e);
            throw new ReqlDriverError("Failed to retrieve connection", e);
        }
    }

    @SuppressWarnings("unused")
    public ConnectionPoolMetrics getMetrics() {
        return new ConnectionPoolMetrics(pool.getNumActive(),
                pool.getNumIdle(),
                pool.getMaxTotal(),
                stats.getConnectionsPerSecond(),
                stats.getAverage(),
                getHealth());
    }

    private ConnectionPoolMetrics.PoolHealth getHealth() {
        if (pool.isClosed()) {
            return ConnectionPoolMetrics.PoolHealth.STOPPED;
        } else if (pool.getNumActive() >= pool.getMaxTotal()) {
            return ConnectionPoolMetrics.PoolHealth.FULL;
        } else {
            return ConnectionPoolMetrics.PoolHealth.HEALTHY;
        }
    }
}
