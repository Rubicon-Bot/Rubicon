package fun.rubicon.cluster_server.cluster.command;

import fun.rubicon.cluster_server.cluster.ClusterServer;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public abstract class ClusterCommand {

    private final String invoke;

    public abstract void execute(ClusterServer clusterServer, String message, String invoke, String[] args, Channel channel);
}
