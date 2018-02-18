/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandWelcomeChannel extends CommandHandler {

    public CommandWelcomeChannel() {
        super(new String[]{"channel", "welcomechannel", "welchannel", "joinchannel", "messagechannel"}, CommandCategory.SETTINGS,
                new PermissionRequirements("command.channel", false, false),
                "Set the Server Welcome Channel!", "<#channel>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Check if Channel got Mentioned
        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() <= 0)
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getParameterUsage()).build()).build();
        //Get the Mentioned Channel
        String ch = parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getId();
        //Update MySql
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "channel", ch);
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark: Successfully set the Event-Channel!").build()).build();
    }
}
