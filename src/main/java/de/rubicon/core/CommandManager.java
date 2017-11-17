package de.rubicon.core;

import de.rubicon.command.CommandCategory;
import de.rubicon.command.CommandHandler;
import de.rubicon.commands.general.CommandHelp;
import de.rubicon.commands.general.CommandInfo;
import de.rubicon.commands.general.CommandPing;
import de.rubicon.commands.fun.CommandRoll;
import de.rubicon.commands.guildowner.CommandSettings;
import de.rubicon.commands.guildowner.CommandStartup;
import de.rubicon.commands.moderation.CommandClear;
import de.rubicon.commands.tools.CommandGoogle;
import de.rubicon.commands.tools.CommandSearch;

public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandHelp("help", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandSearch("search", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandClear("clear", CommandCategory.MODERATION));
        CommandHandler.addCommand(new CommandGoogle("google", CommandCategory.GENERAL));
<<<<<<< HEAD
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL));
=======
        CommandHandler.addCommand(new CommandSettings("settings", CommandCategory.GUILD_OWNER));
        CommandHandler.addCommand(new CommandStartup("startup", CommandCategory.GUILD_OWNER));
>>>>>>> master
    }
}
