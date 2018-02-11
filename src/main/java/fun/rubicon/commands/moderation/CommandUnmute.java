/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

/**
 * Handles the 'unmute' command.
 *
 * @author Michael Rittmeister / Schlaubi
 */
public class CommandUnmute extends CommandHandler {
    public CommandUnmute() {
        super(new String[]{"unmute", "demute"}, CommandCategory.MODERATION, new PermissionRequirements("command.unmute", false, false), "Unmutes users", "<@User>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        Member user = parsedCommandInvocation.getMember();
        Guild guild = parsedCommandInvocation.getGuild();

        if (message.getMentionedUsers().isEmpty())
            return createHelpMessage();
        Member victim = guild.getMember(message.getMentionedUsers().get(0));
        if (!user.canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "You have no permission to interact with " + victim.getAsMention()).build()).build();
        Role muted = CommandMute.createMutedRoleIfNotExists(guild);
        if (!victim.getRoles().contains(muted))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not muted", "This user is not muted").build()).build();
        guild.getController().removeSingleRoleFromMember(victim, muted).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Unmuted", "Successfully unmuted " + victim.getAsMention()).build()).build();

    }
}
