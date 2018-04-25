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
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

public class CommandMoveAll extends CommandHandler {
    public CommandMoveAll() {
        super(new String[]{"moveall", "mvall", "mva"}, CommandCategory.MODERATION, new PermissionRequirements("command.moveall", false, false), "Move all members in your channel into another channel.", "<#channel>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        Message message = parsedCommandInvocation.getMessage();
        Guild guild = parsedCommandInvocation.getMessage().getGuild();
        if (args.length == 0) {
            return createHelpMessage(parsedCommandInvocation);
        }
        if (!message.getMember().getVoiceState().inVoiceChannel())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not connected", "Please connect to a voice channel to use this command").build()).build();

        String name;
        name = message.getContentRaw().replace(parsedCommandInvocation.getCommandInvocation(), "");
        name = name.replace(parsedCommandInvocation.getPrefix(), "");
        name = name.substring(1);
        List<VoiceChannel> channels = message.getGuild().getVoiceChannelsByName(name, true);
        if (channels.isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Channel not found", "This channel doesen't exist").build()).build();
        VoiceChannel channel = channels.get(0);
        if (channel.equals(message.getMember().getVoiceState().getChannel()))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Same channel", "You are already connected to that channel").build()).build();
        GuildController controller = message.getGuild().getController();
        if (!guild.getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Cannot move you!", "Cannot move all members in the Channel").build()).build();
        }
        message.getMember().getVoiceState().getChannel().getMembers().forEach(m -> {
            if (!parsedCommandInvocation.getSelfMember().canInteract(m))
                return;
            controller.moveVoiceMember(m, channel).queue();
        });
        return new MessageBuilder().setEmbed(EmbedUtil.success("Connected", "Connected all users in your channel to `" + channel.getName() + "`").build()).build();
    }
}
