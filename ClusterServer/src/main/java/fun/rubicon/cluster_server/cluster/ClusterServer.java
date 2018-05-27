package fun.rubicon.cluster_server.cluster;

import fun.rubicon.cluster_server.cluster.event.ClusterEventManager;
import io.netty.channel.Channel;

import java.util.List;

public interface ClusterServer {

    /**
     * Simple message broadcast
     * @param invoke the command invoke
     * @param message the message
     */
    void send(String invoke, String message);

    /**
     * Simple message sending to specific channel
     * @param channel the channel
     * @param invoke the command invoke
     * @param message the message
     */
    void send(Channel channel, String invoke, String message);

    /**
     * @return all connected {@link Channel}
     */
    List<Channel> getChannels();

    /**
     * @return the cluster event manager
     */
    ClusterEventManager getEventManager();
}
