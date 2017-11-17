package de.rubicon.core;

import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.commands.general.CommandPing;
import de.rubicon.commands.fun.CommandRoll;
import de.rubicon.commands.moderation.CommandClear;
import de.rubicon.commands.tools.CommandGoogle;

public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.BOT_OWNER));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandClear("clear", CommandCategory.MODERATION));
        CommandHandler.addCommand(new CommandGoogle("google", CommandCategory.GENERAL));
    }
}
