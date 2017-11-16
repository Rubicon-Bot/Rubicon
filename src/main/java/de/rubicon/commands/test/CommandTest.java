package de.rubicon.commands.test;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandTest extends Command {

    public CommandTest(String command, CommandCategory category, CommandCategory subcategory) {
        super(command, category, subcategory);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        sendErrorMessage("Testing se bot");
        sendNoPermissionMessage();
        sendNotImplementedMessage();
        sendUsageMessage();
    }

    @Override
    public String getDescription() {
        return "Test Command";
    }

    @Override
    public String getUsage() {
        return "test";
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
