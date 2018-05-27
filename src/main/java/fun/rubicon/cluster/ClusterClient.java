package fun.rubicon.cluster;

import fun.rubicon.cluster.event.ClusterEventManager;
import io.netty.channel.Channel;

import java.util.List;

public interface ClusterClient {

    void send(String invoke, String message);

    /**
     * @return all connected {@link io.netty.channel.Channel}
     */
    List<Channel> getChannels();

    /**
     * @return the cluster event manager
     */
    ClusterEventManager getEventManager();
}
