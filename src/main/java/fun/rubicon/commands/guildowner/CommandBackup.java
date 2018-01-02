/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.guildowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'backup' command.
 * @author Yannick Seeger / ForYaSee
 */
public class CommandBackup extends CommandHandler {

    public CommandBackup(String[] invocationAliases, CommandCategory category, PermissionRequirements permissionRequirements, String description, String usage) {
        super(invocationAliases, category, permissionRequirements, description, usage);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //TODO Work in Progress
        return null;
    }
}
