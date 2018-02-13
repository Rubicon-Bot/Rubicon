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
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandJoinMessage extends CommandHandler {
    public CommandJoinMessage() {
        super(new String[]{"joinmsg", "joinmessage", "joinnachricht"}, CommandCategory.SETTINGS,
                new PermissionRequirements("command.joinmsg", false, false),
                "Set the server's join message!", "<Message(%user% for username, %guild% for guildname)>\ndisable/off");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 0)
            return createHelpMessage();
        String content = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " ", "");
        if (content.equalsIgnoreCase("disable") || content.equalsIgnoreCase("false") || content.equalsIgnoreCase("0") || content.equalsIgnoreCase("off")) {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "joinmsg", "0");
            return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled!", "Succesfully disabled joinmessages.").build()).build();
        }
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.getMessage().getGuild(), "joinmsg", content);
        return new MessageBuilder().setEmbed(EmbedUtil.success("Enabled!", "Successfully set message to `" + content + "`.").build()).build();
    }
}