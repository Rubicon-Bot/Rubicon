/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Handles the 'mute' command
 */
public class CommandMute extends CommandHandler {
    public CommandMute() {
        super(new String[]{"mute"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.mute"), "Mutes an annoying member", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "mute <@User>").build()).build();
        Member target = message.getGuild().getMember(message.getMentionedUsers().get(0));
        if (!message.getGuild().getSelfMember().canInteract(target))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "Sorry i can't mute this user! His/her role is higher than yours.").build()).build();

        TextChannel channel = message.getTextChannel();
        if (channel.getPermissionOverride(target) == null)
            channel.createPermissionOverride(target).complete();
        if (channel.getPermissionOverride(target).getDenied().contains(Permission.MESSAGE_WRITE))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already muted", "This user is already muted.").build()).build();
        message.getGuild().getTextChannels().forEach(c -> {
            if (c.getPermissionOverride(target) == null)
                c.createPermissionOverride(target).complete();
            c.getPermissionOverride(target).getManager().deny(Permission.MESSAGE_WRITE).queue();
        });
        PrivateChannel targetch = target.getUser().openPrivateChannel().complete();
        targetch.sendMessage(EmbedUtil.info("Muted", "You got muted on `" + message.getGuild().getName() + "` by " + message.getAuthor().getAsMention()).build()).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Muted", "Successfully muted " + target.getAsMention()).build()).build();
    }
}
