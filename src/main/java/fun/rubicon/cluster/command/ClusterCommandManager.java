package fun.rubicon.cluster.command;

import fun.rubicon.cluster.event.ClusterListenerAdapter;
import fun.rubicon.cluster.events.ClusterMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClusterCommandManager extends ClusterListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(ClusterCommandManager.class.getSimpleName());
    private final Map<String, ClusterCommand> commands;

    public ClusterCommandManager() {
        commands = new HashMap<>();
    }

    @Override
    public void onMessageReceived(ClusterMessageReceivedEvent clusterMessageReceivedEvent) {
        String message = clusterMessageReceivedEvent.getMessage();
        String rawArgs[] = message.split(" ");
        String invoke = rawArgs[0];
        invoke = invoke.replace("[", "").replace("]", "").toLowerCase();
        String[] args = new String[rawArgs.length - 1];
        if (args.length != 0)
            System.arraycopy(rawArgs, 1, args, 0, args.length);
        if (commands.containsKey(invoke))
            commands.get(invoke).execute(clusterMessageReceivedEvent.getClusterClient(), message, invoke, args, clusterMessageReceivedEvent.getChannel());
    }

    public void addCommand(ClusterCommand clusterCommand) {
        if (!commands.containsKey(clusterCommand.getInvoke()))
            commands.put(clusterCommand.getInvoke(), clusterCommand);
        else
            logger.warn("Trying to register '{} twice.'", clusterCommand.getInvoke());
    }
}
