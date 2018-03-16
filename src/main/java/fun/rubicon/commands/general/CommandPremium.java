package fun.rubicon.commands.general;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandPremium extends CommandHandler {

    public CommandPremium() {
        super(new String[]{"premium"}, CommandCategory.GENERAL, new PermissionRequirements("premium", false, true), "See your premium state or buy premium.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return null;
    }

}