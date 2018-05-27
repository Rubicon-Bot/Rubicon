package fun.rubicon.cluster_server.cluster.events;

import fun.rubicon.cluster_server.cluster.ClusterServer;
import fun.rubicon.cluster_server.cluster.event.ClusterEvent;
import io.netty.channel.Channel;
import lombok.Getter;

public class ClusterConnectedEvent extends ClusterEvent implements ReplyableEvent {

    @Getter private final Channel channel;

    public ClusterConnectedEvent(ClusterServer clusterServer, Channel channel) {
        super(clusterServer);
        this.channel = channel;
    }

    @Override
    public void reply(String invoke, String message) {
        getClusterServer().send(channel, invoke, message);
    }
}
