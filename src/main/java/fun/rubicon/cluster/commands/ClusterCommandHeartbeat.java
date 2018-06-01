package fun.rubicon.cluster.commands;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.command.ClusterCommand;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterCommandHeartbeat extends ClusterCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public ClusterCommandHeartbeat() {
        super("heartbeat");
    }

    @Override
    public void execute(ClusterClient clusterClient, String message, String invoke, String[] args, Channel channel) {
        logger.info("Heartbeat from: {}", channel.remoteAddress());
    }
}
