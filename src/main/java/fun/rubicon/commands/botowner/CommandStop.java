/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'stop' command.
 * @author Leon Kappes / Lee
 */
public class CommandStop extends CommandHandler {

    public CommandStop() {
        super(new String[]{"stop"}, CommandCategory.BOT_OWNER, new PermissionRequirements(4, "command.stop"), "Stops the bot.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (RubiconBot.getMySQL() != null) {
            RubiconBot.getMySQL().disconnect();
        }
        parsedCommandInvocation.invocationMessage.getTextChannel().sendMessage(new EmbedBuilder().setDescription(":battery: System Shutdown :battery:").build()).queue();
        System.exit(0);
        return null;
    }


}
