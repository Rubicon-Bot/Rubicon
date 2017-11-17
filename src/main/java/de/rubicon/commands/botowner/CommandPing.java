package de.rubicon.commands.botowner;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.concurrent.TimeUnit;

public class CommandPing extends Command{


    public CommandPing(String command, CommandCategory category, CommandCategory subcategory) {
        super(command, category, subcategory);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        e.getTextChannel().sendMessage("Pong! Bot online.").queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    @Override
    public String getDescription() {
        return "Checking bot online status.";
    }

    @Override
    public String getUsage() {
        return "ping";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
