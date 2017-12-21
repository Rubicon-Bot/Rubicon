/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.commands.fun.CommandGiveaway;
import fun.rubicon.commands.admin.CommandPermission;
import fun.rubicon.commands.general.CommandMusic;

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
        CommandHandler.addCommand(new CommandPermission("permission", CommandCategory.ADMIN).addAliases("perm", "perms"));
        CommandHandler.addCommand(new CommandGiveaway("giveaway", CommandCategory.MODERATION).addAliases("g"));
        CommandHandler.addCommand(new CommandMusic("music", CommandCategory.GENERAL).addAliases("m"));
    }
}
