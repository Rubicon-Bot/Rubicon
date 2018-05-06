package de.jakobjarosch.rethinkdb.pool;


public class ConnectionPoolMetrics {

    public enum PoolHealth {
        /**
         * Everything fine.
         */
        HEALTHY,
        /**
         * No free connections left, and maxConnections reached.
         */
        FULL,
        STOPPED
    }

    private final int connections;
    private final int freeConnections;
    private final int maxConnections;
    private final double connectionsPerSecond;
    private final double averageWaitTime;
    private final PoolHealth poolHealth;

    ConnectionPoolMetrics(int connections,
                          int freeConnections,
                          int maxConnections,
                          double connectionsPerSecond,
                          double averageWaitTime,
                          PoolHealth poolHealth) {
        this.connections = connections;
        this.freeConnections = freeConnections;
        this.maxConnections = maxConnections;
        this.connectionsPerSecond = connectionsPerSecond;
        this.averageWaitTime = averageWaitTime;
        this.poolHealth = poolHealth;
    }

    @SuppressWarnings("unused")
    public int getConnections() {
        return connections;
    }

    @SuppressWarnings("unused")
    public int getFreeConnections() {
        return freeConnections;
    }

    @SuppressWarnings("unused")
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * @return The average connection used per second in the last 5 minutes.
     */
    @SuppressWarnings("unused")
    public double getConnectionsPerSecond() {
        return connectionsPerSecond;
    }

    /**
     * @return The average wait time to obtain a connection in the last 5 minutes. Unit is milliseconds.
     */
    @SuppressWarnings("unused")
    public double getAverageWaitTime() {
        return averageWaitTime;
    }

    @SuppressWarnings("unused")
    public PoolHealth getPoolHealth() {
        return poolHealth;
    }
}
