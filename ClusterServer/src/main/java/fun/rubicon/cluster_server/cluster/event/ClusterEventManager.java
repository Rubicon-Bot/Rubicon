package fun.rubicon.cluster_server.cluster.event;

import lombok.Data;

import java.util.List;

@Data
public class ClusterEventManager {

    private final List<ClusterListenerAdapter> listenerAdapters;

    public void fire(ClusterEvent clusterEvent) {
        for (ClusterListenerAdapter listenerAdapter : listenerAdapters)
            listenerAdapter.onEvent(clusterEvent);
    }
}
