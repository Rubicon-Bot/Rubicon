package de.rubicon.commands.general;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandInfo extends Command {

    public CommandInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {

    }

    @Override
    public String getDescription() {
        return "Shows some information about the bot!";
    }

    @Override
    public String getUsage() {
        return "info";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
