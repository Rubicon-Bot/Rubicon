/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandGiveaway;
import fun.rubicon.commands.admin.CommandPermission;
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.botowner.*;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
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
        CommandHandler.addCommand(new CommandGiveaway("giveaway", CommandCategory.ADMIN).addAliases("g"));
        CommandHandler.addCommand(new CommandPortal("portal", CommandCategory.ADMIN).addAliases("mirror"));
        CommandHandler.addCommand(new CommandAutochannel("autochannel", CommandCategory.ADMIN).addAliases("ac", "autoc"));

        //BotOwner
        CommandHandler.addCommand(new CommandStop("stop", CommandCategory.BOT_OWNER).addAliases("terminate"));
        CommandHandler.addCommand(new CommandEval("eval", CommandCategory.BOT_OWNER).addAliases("e"));
        CommandHandler.addCommand(new CommandSetmoney("setmoney", CommandCategory.BOT_OWNER).addAliases("moneyset"));


        //Fun
        CommandHandler.addCommand(new CommandLmgtfy("lmgtfy", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandColor("color", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandChoose("choose", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandLevel("rank", CommandCategory.FUN).addAliases("lvl", "level"));
        //CommandHandler.addCommand(new CommandBday("birthday", CommandCategory.FUN).addAliases("bday"));
        CommandHandler.addCommand(new CommandRip("rip", CommandCategory.FUN).addAliases("tombstone"));
        CommandHandler.addCommand(new CommandRoulette("roulette", CommandCategory.BOT_OWNER).addAliases("roulete","rulette", "roullete"));


        //general
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL).addAliases("inf"));
        CommandHandler.addCommand(new CommandSpeedTest("speedtest", CommandCategory.GENERAL).addAliases("st"));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL).addAliases("stats"));
        CommandHandler.addCommand(new CommandInvite("invite", CommandCategory.GENERAL).addAliases("inv"));
        CommandHandler.addCommand(new CommandBug("bug", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandFeedback("feedback", CommandCategory.GENERAL).addAliases("fedback"));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));
        //CommandHandler.addCommand(new CommandDonatemoney("givemoney", CommandCategory.GENERAL).addAliases("donatemoney", "domo", "modo"));




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
