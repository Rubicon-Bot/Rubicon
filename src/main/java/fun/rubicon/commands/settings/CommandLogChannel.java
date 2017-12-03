/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'logchannel' command which can change the channel all command-logs of a guild are posted to.
 * @author LeeDJD
 */
public class CommandLogChannel extends CommandHandler {
    /**
     * Constructs this CommandHandler.
     */
    public CommandLogChannel() {
        super(new String[]{"logchannel", "lch", "log"}, CommandCategory.SETTINGS,
                new PermissionRequirements(2, "command.logchannel"),
                "Set the Server LogChannel.", "logchannel <#channel>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Check if Channel got Mentioned
        if (parsedCommandInvocation.invocationMessage.getMentionedChannels().size() <= 0)
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getUsage()).build()).build();
        //Get the Mentioned Channel
        String txt = parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getId();
        //Update MySql
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "logchannel", txt);
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark: Successfully set the LogChannel!").build()).build();
    }
}
