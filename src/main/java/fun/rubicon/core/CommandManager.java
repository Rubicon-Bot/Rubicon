/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandGivaway;
import fun.rubicon.commands.admin.CommandPermission;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.botowner.CommandBroadcast;
import fun.rubicon.commands.botowner.CommandEval;
import fun.rubicon.commands.botowner.CommandRestart;
import fun.rubicon.commands.botowner.CommandStop;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.guildowner.CommandBackup;
import fun.rubicon.commands.guildowner.CommandPrefix;
import fun.rubicon.commands.guildowner.CommandSettings;
import fun.rubicon.commands.tools.*;

/**
 * Old command registration script.
 * @author Yannick Seeger / ForYaSee
 * @deprecated Register commands in RubiconBot.registerCommandHandlers() instead.
 * @see fun.rubicon.RubiconBot
 */
@Deprecated
public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
        //Admin
        CommandHandler.addCommand(new CommandPermission("permission", CommandCategory.ADMIN).addAliases("permission", "perm", "perms"));
        CommandHandler.addCommand(new CommandGivaway("giveaway", CommandCategory.ADMIN).addAliases("g"));
        CommandHandler.addCommand(new CommandPortal("portal", CommandCategory.ADMIN).addAliases("mirror"));
        CommandHandler.addCommand(new CommandAutochannel("autochannel", CommandCategory.ADMIN).addAliases("ac", "autoc"));

        //BotOwner
        CommandHandler.addCommand(new CommandBroadcast("broadcast", CommandCategory.BOT_OWNER).addAliases("sayb","bsay", "br"));
        CommandHandler.addCommand(new CommandRestart("restart", CommandCategory.BOT_OWNER).addAliases("r","re", "rs"));
        CommandHandler.addCommand(new CommandStop("stop", CommandCategory.BOT_OWNER).addAliases("terminate"));
        CommandHandler.addCommand(new CommandEval("eval", CommandCategory.BOT_OWNER).addAliases("e"));

        //Fun
        //CommandHandler.addCommand(new CommandJoke("joke", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandLmgtfy("lmgtfy", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandColor("color", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandChoose("choose", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandLevel("rank", CommandCategory.FUN).addAliases("lvl", "level"));
        CommandHandler.addCommand(new CommandBday("birthday", CommandCategory.FUN).addAliases("bday"));
        CommandHandler.addCommand(new CommandShort("short", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandShort("short", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandRip("rip", CommandCategory.FUN).addAliases("tombstone"));


        //general
        CommandHandler.addCommand(new CommandHelp("help", CommandCategory.GENERAL).addAliases("h"));
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL).addAliases("inf"));
        CommandHandler.addCommand(new CommandPing("ping", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandSpeedTest("speedtest", CommandCategory.GENERAL).addAliases("st"));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL).addAliases("stats"));
        CommandHandler.addCommand(new CommandInvite("invite", CommandCategory.GENERAL).addAliases("inv"));
        CommandHandler.addCommand(new CommandBug("bug", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandFeedback("feedback", CommandCategory.GENERAL).addAliases("fedback"));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));


        //Guildowner
        CommandHandler.addCommand(new CommandSettings("settings", CommandCategory.GUILD_OWNER));
        CommandHandler.addCommand(new CommandBackup("backup", CommandCategory.GUILD_OWNER));

        //Settings
        CommandHandler.addCommand(new CommandPrefix("prefix", CommandCategory.SETTINGS).addAliases("pref"));

        //Tools
        CommandHandler.addCommand(new CommandClear("clear", CommandCategory.TOOLS).addAliases("purge"));
        CommandHandler.addCommand(new CommandGoogle("google", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandSearch("search", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandServerInfo("serverinfo", CommandCategory.TOOLS).addAliases("guild","guildinfo"));
        CommandHandler.addCommand(new CommandUserInfo("userinfo", CommandCategory.TOOLS).addAliases("whois"));
        CommandHandler.addCommand(new CommandSay("say", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandVote("vote", CommandCategory.TOOLS).addAliases("v"));
        CommandHandler.addCommand(new CommandASCII("ascii", CommandCategory.TOOLS));
        //CommandHandler.addCommand(new CommandBrainfuck("brainfuck", CommandCategory.TOOLS).addAliases("bf"));
    }
}
