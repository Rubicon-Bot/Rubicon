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
 * Handles the 'unmute' command.
 * @author Michael Rittmeister / Schlaubi
 */
public class CommandUnmute extends CommandHandler {
    public CommandUnmute() {
        super(new String[]{"unmute", "demute"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.unmute"), "Unmutes users", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.invocationMessage;
        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "unmute <@User>").build()).build();
        Member target = message.getGuild().getMember(message.getMentionedUsers().get(0));
        if (!message.getGuild().getSelfMember().canInteract(target))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "Sorry i can't unmute this use! It's a moderator or higher").build()).build();

        TextChannel channel = message.getTextChannel();
        if (channel.getPermissionOverride(target) == null)
            channel.createPermissionOverride(target).complete();
        if (!channel.getPermissionOverride(target).getDenied().contains(Permission.MESSAGE_WRITE))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not muted", "This user is no muted. Use `mute <@User>` to mute him").build()).build();
        message.getGuild().getTextChannels().forEach(c -> {
            if (c.getPermissionOverride(target) == null)
                c.createPermissionOverride(target).complete();
            c.getPermissionOverride(target).getManager().grant(Permission.MESSAGE_WRITE).queue();
        });
        PrivateChannel targetch = target.getUser().openPrivateChannel().complete();
        targetch.sendMessage(EmbedUtil.info("Unmuted", "You got unmuted on `" + message.getGuild().getName() + "` by " + message.getAuthor().getAsMention()).build()).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Unmuted", "Successfully Unmuted " + target.getAsMention()).build()).build();

    }
}
