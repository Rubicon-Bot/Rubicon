/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

public class CommandSay extends CommandHandler {

    public CommandSay() {
        super(new String[]{"say", "s"}, CommandCategory.GENERAL, new PermissionRequirements("command.say", false, true), "Send a Message as the Bot!", "<Channel> <Message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length < 2) {
            return createHelpMessage();
        }

        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() == 0) {
            return createHelpMessage();
        }
        TextChannel textChannel = parsedCommandInvocation.getMessage().getMentionedChannels().get(0);
        if (!parsedCommandInvocation.getSelfMember().hasPermission(textChannel, Permission.MESSAGE_READ)) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I have no permissions to write in this channel."));
        }
        String text = parsedCommandInvocation.getMessage().getContentDisplay().replace(parsedCommandInvocation.getPrefix() + parsedCommandInvocation.getCommandInvocation() + " #" + textChannel.getName(), "");
        if (!parsedCommandInvocation.getArgs()[0].contains("#")) {
            return EmbedUtil.message(EmbedUtil.error("No channel!", "Your first parameter must be a #channel."));
        }

        if (parsedCommandInvocation.getMessage().getMentionedUsers().size() >= 1) {
            for (User user : parsedCommandInvocation.getMessage().getMentionedUsers()) {
                Member member = parsedCommandInvocation.getGuild().getMember(user);
                text = text.replace("@" + member.getEffectiveName(), member.getAsMention());
            }
        }
        if (parsedCommandInvocation.getMessage().getMentionedRoles().size() >= 1) {
            for (Role role : parsedCommandInvocation.getMessage().getMentionedRoles()) {
                text = text.replace("@" + role.getName(), role.getAsMention());
            }
        }
        if (parsedCommandInvocation.getMessage().getMentionedChannels().size() >= 2) {
            for (int i = 1; i < parsedCommandInvocation.getMessage().getMentionedChannels().size(); i++) {
                TextChannel channel = parsedCommandInvocation.getMessage().getMentionedChannels().get(i);
                text = text.replace("#" + channel.getName(), channel.getAsMention());
            }
        }
        textChannel.sendMessage(text).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Successful", "Successfully sent message in " + textChannel.getAsMention()).build()).build();
    }
}
