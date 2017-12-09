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
<<<<<<< HEAD
import fun.rubicon.commands.botowner.CommandEval;
import fun.rubicon.commands.fun.CommandRoulette;
import fun.rubicon.commands.general.CommandMusic;
import fun.rubicon.commands.general.CommandStatistics;
=======
import fun.rubicon.commands.admin.CommandPortal;
import fun.rubicon.commands.fun.*;
import fun.rubicon.commands.general.*;
import fun.rubicon.commands.tools.*;
>>>>>>> master

/**
 * Old command registration script.
 *
 * @author Yannick Seeger / ForYaSee
 * @see fun.rubicon.RubiconBot
 * @deprecated Register commands in RubiconBot.registerCommandHandlers() instead.
 */
@Deprecated
public class CommandManager {

    public CommandManager() {
        initCommands();
    }

    private void initCommands() {
<<<<<<< HEAD
=======
        //Admin
        //TODO All Trash lol
>>>>>>> master
        CommandHandler.addCommand(new CommandPermission("permission", CommandCategory.ADMIN).addAliases("permission", "perm", "perms"));
        CommandHandler.addCommand(new CommandGiveaway("giveaway", CommandCategory.ADMIN).addAliases("g"));
        CommandHandler.addCommand(new CommandAutochannel("autochannel", CommandCategory.ADMIN).addAliases("ac", "autoc"));
<<<<<<< HEAD
        CommandHandler.addCommand(new CommandEval("eval", CommandCategory.BOT_OWNER).addAliases("e"));
        CommandHandler.addCommand(new CommandRoulette("roulette", CommandCategory.BOT_OWNER).addAliases("roulete", "rulette", "roullete"));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL).addAliases("stats"));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));
=======



        //Fun
        CommandHandler.addCommand(new CommandRoll("roll", CommandCategory.FUN));
        CommandHandler.addCommand(new CommandChoose("choose", CommandCategory.FUN));
        //CommandHandler.addCommand(new CommandBday("birthday", CommandCategory.FUN).addAliases("bday"));
        CommandHandler.addCommand(new CommandRoulette("roulette", CommandCategory.BOT_OWNER).addAliases("roulete","rulette", "roullete"));


        //general
        CommandHandler.addCommand(new CommandInfo("info", CommandCategory.GENERAL).addAliases("inf"));
        CommandHandler.addCommand(new CommandSpeedTest("speedtest", CommandCategory.GENERAL).addAliases("st"));
        CommandHandler.addCommand(new CommandStatistics("statistics", CommandCategory.GENERAL).addAliases("stats"));
        CommandHandler.addCommand(new CommandBug("bug", CommandCategory.GENERAL));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));
        //CommandHandler.addCommand(new CommandDonatemoney("givemoney", CommandCategory.GENERAL).addAliases("donatemoney", "domo", "modo"));




        /*//Tools
        CommandHandler.addCommand(new CommandGoogle("google", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandSearch("search", CommandCategory.TOOLS));
        CommandHandler.addCommand(new CommandServerInfo("serverinfo", CommandCategory.TOOLS).addAliases("guild","guildinfo"));
        CommandHandler.addCommand(new CommandUserInfo("userinfo", CommandCategory.TOOLS).addAliases("whois"));
        CommandHandler.addCommand(new CommandVote("vote", CommandCategory.TOOLS).addAliases("v"));*/
        //CommandHandler.addCommand(new CommandBrainfuck("brainfuck", CommandCategory.TOOLS).addAliases("bf"));
>>>>>>> master
    }
}
