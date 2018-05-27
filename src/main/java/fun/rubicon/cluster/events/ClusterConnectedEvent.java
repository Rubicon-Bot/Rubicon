package fun.rubicon.cluster.events;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.event.ClusterEvent;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClusterConnectedEvent extends ClusterEvent {

    @Getter private final Channel channel;

    public ClusterConnectedEvent(ClusterClient clusterClient, Channel channel) {
        super(clusterClient);
        this.channel = channel;
    }
}
