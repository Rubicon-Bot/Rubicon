package fun.rubicon.cluster_server.cluster.events;

import fun.rubicon.cluster_server.cluster.ClusterServer;
import fun.rubicon.cluster_server.cluster.event.ClusterEvent;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClusterDisconnectedEvent extends ClusterEvent {

    @Getter
    private final Channel channel;

    public ClusterDisconnectedEvent(ClusterServer clusterServer, Channel channel) {
        super(clusterServer);
        this.channel = channel;
    }
}
