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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Handles the 'ban' command.
 *
 * @author Michael Rittmeister / Schlaubi
 */
public class CommandBan extends CommandHandler {
    public CommandBan() {
        super(new String[]{"ban"}, CommandCategory.MODERATION, new PermissionRequirements("command.ban", false, false), "Bans a user from your server", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message msg = parsedCommandInvocation.getMessage();
        if (msg.getMentionedUsers().isEmpty()) {
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "ban <@User>").build()).build();
        }
        Member target = msg.getGuild().getMember(msg.getMentionedUsers().get(0));
        if (!msg.getGuild().getSelfMember().canInteract(target)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "Sorry I can't ban this User.").build()).build();
        } else {
            if (!target.getUser().isBot()) {
                PrivateChannel channel = target.getUser().openPrivateChannel().complete();
                channel.sendMessage(EmbedUtil.success("Banned", "You got banned").build()).queue();
            }
            msg.getGuild().getController().ban(target, 7).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Banned", "Successfully banned " + target.getAsMention()).build()).build();
        }
    }
}
