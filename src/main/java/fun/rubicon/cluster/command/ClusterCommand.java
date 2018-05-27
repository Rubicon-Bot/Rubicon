package fun.rubicon.cluster.command;

import fun.rubicon.cluster.ClusterClient;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public abstract class ClusterCommand {

    private final String invoke;

    public abstract void execute(ClusterClient clusterClient, String message, String invoke, String[] args, Channel channel);
}
