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
import net.dv8tion.jda.core.entities.*;

/**
 * Handles the 'unmute' command.
 * @author Michael Rittmeister / Schlaubi
 */
public class CommandUnmute extends CommandHandler {
    public CommandUnmute() {
        super(new String[]{"unmute", "demute"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.unmute"), "Unmutes users", "<@User>",false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        Member user = parsedCommandInvocation.getMember();
        Guild guild = parsedCommandInvocation.getGuild();

        if(message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "mute <@User>").build()).build();
        Member victim = guild.getMember(message.getMentionedUsers().get(0));
        if(!user.canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "You have no permission to interact with " + victim.getAsMention()).build()).build();
        Role muted = CommandMute.createMutedRoleIfNotExists(guild);
        if(!victim.getRoles().contains(muted))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not muted", "This user is not muted").build()).build();
        guild.getController().removeSingleRoleFromMember(victim, muted).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Unmuted", "Successfully unmuted " + victim.getAsMention()).build()).build();

    }
}
