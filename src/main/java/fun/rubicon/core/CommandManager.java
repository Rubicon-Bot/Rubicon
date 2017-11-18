package fun.rubicon.core;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.admin.CommandPermission;
import fun.rubicon.commands.botowner.CommandBroadcast;
import fun.rubicon.commands.botowner.CommandRestart;
import fun.rubicon.commands.botowner.CommandStop;
import fun.rubicon.commands.fun.CommandJoke;
import fun.rubicon.commands.fun.CommandLmgtfy;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.fun.CommandRoll;
import fun.rubicon.commands.guildowner.CommandSettings;
import fun.rubicon.commands.guildowner.CommandStartup;
import fun.rubicon.commands.tools.*;

public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        //Admin
        CommandHandler.addCommand(new CommandPermission("permission", CommandCategory.ADMIN));

        //BotOwner
        CommandHandler.addCommand(new CommandBroadcast("broadcast", CommandCategory.BOT_OWNER));
        CommandHandler.addCommand(new CommandRestart("restart", CommandCategory.BOT_OWNER));
        CommandHandler.addCommand(new CommandStop("stop", CommandCategory.BOT_OWNER));

        //Fun
        CommandHandler.addCommand(new CommandJoke("joke", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandLmgtfy("lmgtfy", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));

        //general
        CommandHandler.addCommand(new CommandHelp("help", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandSpeedTest("speedtest", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandInvite("invite", CommandCategory.GENERAL));

        //Guildowner
        CommandHandler.addCommand(new CommandSettings("settings", CommandCategory.GUILD_OWNER));
        CommandHandler.addCommand(new CommandStartup("startup", CommandCategory.GUILD_OWNER));

        //Tools
        CommandHandler.addCommand(new CommandClear("clear", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandGoogle("google", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandSearch("search", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandServerInfo("serverinfo", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandUserInfo("userinfo", CommandCategory.TOOLS));
    }
}
