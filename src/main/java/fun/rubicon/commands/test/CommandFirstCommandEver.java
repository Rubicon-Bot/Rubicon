/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.test;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandFirstCommandEver extends CommandHandler {

    public CommandFirstCommandEver() {
        super(new String[]{"firstcommandever"}, CommandCategory.TEST, new PermissionRequirements("firstcommand", false, true), "It's the first RubiconBot command.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation command, UserPermissions userPermissions) {
        command.getTextChannel().sendMessage("Yeah! The very first Rubicon Command! :scream: SCHLAUBI IS DEPREACETD").queue();
        return null;
    }
}
