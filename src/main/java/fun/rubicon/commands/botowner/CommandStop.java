/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'stop' command.
 *
 * @author Leon Kappes / Lee
 */
public class CommandStop extends CommandHandler {

    public CommandStop() {
        super(new String[]{"botstop"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.botstop", true, false), "Stops the bot.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        parsedCommandInvocation.getMessage().getTextChannel().sendMessage(new EmbedBuilder().setDescription(":battery: System Shutdown :battery:").build()).queue();
        System.exit(0);
        return null;
    }
}
