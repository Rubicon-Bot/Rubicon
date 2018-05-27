package fun.rubicon.cluster_server.cluster.event;

import fun.rubicon.cluster_server.cluster.ClusterServer;
import lombok.Getter;

public abstract class ClusterEvent {

    @Getter private final ClusterServer clusterServer;

    public ClusterEvent(ClusterServer clusterServer) {
        this.clusterServer = clusterServer;
    }
}
