package de.rubicon.commands.botowner;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandPing extends Command{


    public CommandPing(String command, CommandCategory category, CommandCategory subcategory) {
        super(command, category, subcategory);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        e.getTextChannel().sendMessage("Pong! Bot online.").queue();
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
