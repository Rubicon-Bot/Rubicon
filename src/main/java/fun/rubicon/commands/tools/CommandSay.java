/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class CommandSay extends CommandHandler {

    public CommandSay() {
        super(new String[]{"say", "s"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.say"), "Send a Message as the Bot!", "<Channel> <Message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length < 2) {
            return createHelpMessage();
        }

        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() != 1) {
            return createHelpMessage();
        }
        TextChannel textChannel = parsedCommandInvocation.getMessage().getMentionedChannels().get(0);
        if (!parsedCommandInvocation.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ)) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I have no permissions to write in this channel."));
        }
        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " #" + textChannel.getName(), "");
        textChannel.sendMessage(text).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Successful", "Successfully sent message in " + textChannel.getAsMention()).build()).build();
    }
}
