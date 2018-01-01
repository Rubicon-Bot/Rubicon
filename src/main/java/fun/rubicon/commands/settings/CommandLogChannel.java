/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author LeeDJD
 * @deprecated
 */
public class CommandLogChannel extends CommandHandler {
    /**
     * Constructs this CommandHandler.
     */
    public CommandLogChannel() {
        super(new String[]{"logchannel"}, CommandCategory.SETTINGS,
                new PermissionRequirements(PermissionLevel.ADMINISTRATOR, "command.logchannel"),
                "(Command is deprecated)", "<#channel>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        return EmbedUtil.message(EmbedUtil.info("Command is deprecated", "Please use rc!log"));
    }
}
