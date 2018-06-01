package fun.rubicon.cluster.events;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.event.ClusterEvent;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClusterDisconnectedEvent extends ClusterEvent {

    @Getter private final Channel channel;

    public ClusterDisconnectedEvent(ClusterClient clusterClient, Channel channel) {
        super(clusterClient);
        this.channel = channel;
    }
}
