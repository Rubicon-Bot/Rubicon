package fun.rubicon.cluster.event;

import fun.rubicon.cluster.ClusterClient;
import lombok.Getter;

public abstract class ClusterEvent {

    @Getter private final ClusterClient clusterClient;

    public ClusterEvent(ClusterClient clusterClient) {
        this.clusterClient = clusterClient;
    }
}
