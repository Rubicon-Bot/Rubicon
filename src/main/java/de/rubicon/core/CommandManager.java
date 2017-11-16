package de.rubicon.core;

import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.commands.botowner.CommandPing;
import de.rubicon.commands.test.CommandTest;

public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        CommandHandler.addCommand(new CommandTest("test", CommandCategory.TEST, CommandCategory.TEST));
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.TEST, CommandCategory.TEST));
    }
}
