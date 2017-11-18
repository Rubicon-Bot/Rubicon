package fun.rubicon.core;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.botowner.CommandRestart;
import fun.rubicon.commands.botowner.CommandStop;
import fun.rubicon.commands.fun.CommandJoke;
import fun.rubicon.commands.fun.CommandLmgtfy;
import fun.rubicon.commands.general.CommandHelp;
import fun.rubicon.commands.general.CommandInfo;
import fun.rubicon.commands.general.CommandPing;
import fun.rubicon.commands.fun.CommandRoll;
import fun.rubicon.commands.general.CommandSpeedTest;
import fun.rubicon.commands.guildowner.CommandSettings;
import fun.rubicon.commands.guildowner.CommandStartup;
import fun.rubicon.commands.moderation.CommandClear;
import fun.rubicon.commands.tools.CommandGoogle;
import fun.rubicon.commands.tools.CommandSearch;
import fun.rubicon.commands.tools.CommandServerInfo;
import fun.rubicon.commands.tools.CommandUserInfo;

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
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandSettings("settings", CommandCategory.GUILD_OWNER));
        CommandHandler.addCommand(new CommandStartup("startup", CommandCategory.GUILD_OWNER));
        CommandHandler.addCommand(new CommandStop("stop", CommandCategory.BOT_OWNER));
        CommandHandler.addCommand(new CommandJoke("joke", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandRestart("restart", CommandCategory.BOT_OWNER));
        CommandHandler.addCommand(new CommandServerInfo("serverinfo", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandSpeedTest("speedtest", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandUserInfo("userinfo", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandLmgtfy("lmgtfy", CommandCategory.FUN));
    }
}
