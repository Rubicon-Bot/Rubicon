package de.rubicon.core;

import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.commands.botowner.CommandPing;
import de.rubicon.commands.fun.CommandRoll;

public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.BOT_OWNER, CommandCategory.SUB_NONE));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN, CommandCategory.SUB_NONE));
    }
}
