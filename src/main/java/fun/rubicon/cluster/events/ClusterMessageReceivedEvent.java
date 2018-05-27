package fun.rubicon.cluster.events;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.event.ClusterEvent;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClusterMessageReceivedEvent extends ClusterEvent {

    @Getter private final Channel channel;
    @Getter private final String message;

    public ClusterMessageReceivedEvent(ClusterClient clusterClient, Channel channel, String message) {
        super(clusterClient);
        this.channel = channel;
        this.message = message;
    }
}
