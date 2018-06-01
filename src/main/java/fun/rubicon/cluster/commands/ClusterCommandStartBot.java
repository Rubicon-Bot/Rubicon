package fun.rubicon.cluster.commands;

import fun.rubicon.BotLauncher;
import fun.rubicon.RubiconBot;
import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.command.ClusterCommand;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClusterCommandStartBot extends ClusterCommand {

    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    public ClusterCommandStartBot() {
        super("start_bot");
    }

    @Override
    public void execute(ClusterClient clusterClient, String message, String invoke, String[] args, Channel channel) {
        new RubiconBot(BotLauncher.getClusterClient(), BotLauncher.getClusterCommandManager());
    }
}
