package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.concurrent.TimeUnit;

public class CommandPing extends Command{


    public CommandPing(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        long ping = e.getJDA().getPing();
        e.getChannel().sendMessage("Bot online!").queue(msg -> msg.editMessage("Ping: " + ping + "ms").queueAfter(2, TimeUnit.SECONDS));
    }

    @Override
    public String getDescription() {
        return "Checking Bot ping.";
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
