/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandAFK extends CommandHandler {

    public CommandAFK() {
        super(new String[]{"afk"}, CommandCategory.GENERAL, new PermissionRequirements("akf", false, true), "Shows if you are afk or not and notifies other users.", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation command, UserPermissions userPermissions) {
        RubiconUser rubiconUser = RubiconUser.fromUser(command.getAuthor());

        if(command.getArgs().length == 0) {

        }
        return null;
    }
}
