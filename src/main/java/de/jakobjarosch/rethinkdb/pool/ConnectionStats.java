package de.jakobjarosch.rethinkdb.pool;


import java.util.HashMap;
import java.util.Map;

class ConnectionStats {

    private static class Metric {
        long duration;
    }

    private static final long TIMEOUT = 5 * 60 * 1_000; // 5 minutes

    private final Map<Metric, Long> stats = new HashMap<>();
    private final long startTime = System.currentTimeMillis();

    private long lastCleanup = 0;


    void add(long duration) {
        final Metric metric = new Metric();
        metric.duration = duration;
        stats.put(metric, System.currentTimeMillis());
        cleanup();
    }

    double getAverage() {
        return stats.keySet().stream().mapToLong(m -> m.duration).average().orElse(0);
    }

    double getConnectionsPerSecond() {
        long timeWindow = Math.min(System.currentTimeMillis() - startTime, TIMEOUT);
        return (double) stats.size() / timeWindow;
    }

    private void cleanup() {
        if (lastCleanup + 1000 < System.currentTimeMillis()) {
            lastCleanup = System.currentTimeMillis();
            stats.entrySet().removeIf(e -> e.getValue() < System.currentTimeMillis() - TIMEOUT);
        }
    }
}
