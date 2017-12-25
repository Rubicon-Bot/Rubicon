/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Handles the 'kick' command.
 * @author Michael Rittmeister / Schlaubi
 */
public class CommandKick extends CommandHandler {
    public CommandKick() {
        super(new String[]{"kick"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.kick"), "Kicks an member out of your server", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message msg = parsedCommandInvocation.invocationMessage;
        if (msg.getMentionedUsers().isEmpty()) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "kick <@User>").build()).build();
        }
        Member target = msg.getGuild().getMember(msg.getMentionedUsers().get(0));
        if (!msg.getGuild().getSelfMember().canInteract(target)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "Sorry I can't kick this User!!!").build()).build();
        } else {
            PrivateChannel channel = target.getUser().openPrivateChannel().complete();
            channel.sendMessage(EmbedUtil.success("Kicked", "You got kicked").build()).queue();
            msg.getGuild().getController().kick(target).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Kicked", "Succesfully kicked" + target.getAsMention()).build()).build();
        }
    }
}
