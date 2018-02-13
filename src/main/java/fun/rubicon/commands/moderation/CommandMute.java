/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.moderation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

/**
 * Handles the 'mute' command
 */
public class CommandMute extends CommandHandler {
    public CommandMute() {
        super(new String[]{"mute"}, CommandCategory.MODERATION, new PermissionRequirements("command.mute", false, false), "Mutes an annoying member", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        Member user = parsedCommandInvocation.getMember();
        Guild guild = parsedCommandInvocation.getGuild();

        if (message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "mute <@User>").build()).build();
        Member victim = guild.getMember(message.getMentionedUsers().get(0));
        if(victim.equals(guild.getSelfMember()))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Nice try m8", "PLEASE DO NOT MUTE ME").build()).build();
        if (!user.canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "You have no permission to interact with " + victim.getAsMention()).build()).build();
        if (!guild.getSelfMember().hasPermission(Permission.MANAGE_PERMISSIONS)) {
            return EmbedUtil.message(EmbedUtil.error("Permission Error!", "I need the MANAGE_PERMISSIONS permissions to mute users."));
        }
        Role muted = createMutedRoleIfNotExists(guild);
        if (victim.getRoles().contains(muted))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already muted", "This user is already muted").build()).build();
        try {
            guild.getController().addSingleRoleToMember(victim, muted).queue();
        } catch (HierarchyException e){
            SafeMessage.sendMessage(guild.getDefaultChannel(),"ERROR: Please give me `MANAGE_ROLE` permission to use mute command and move the Rubicon Role to the top", 5);
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("Muted", "Successfully muted " + victim.getAsMention()).build()).build();
    }

    public static Role createMutedRoleIfNotExists(Guild guild) {
        if (!guild.getRolesByName("rubicon-muted", false).isEmpty())
            return guild.getRolesByName("rubicon-muted", false).get(0);
        Role muted = null;
        try {
            muted = guild.getController().createRole().setName("rubicon-muted").complete();
        } catch (InsufficientPermissionException | HierarchyException e) {
            guild.getDefaultChannel().sendMessage("ERROR: Please give me `MANAGE_ROLE` permission to use mute command and move the Rubicon Role to the top");
        }
        Role finalMuted = muted;
        guild.getTextChannels().forEach(c -> {
            try {
                PermissionOverride override = c.createPermissionOverride(finalMuted).complete();
                if (override.getDenied().contains(Permission.MESSAGE_WRITE)) return;
                override.getManager().deny(Permission.MESSAGE_WRITE).queue();
                override.getManager().deny(Permission.MESSAGE_ADD_REACTION).queue();
            } catch (InsufficientPermissionException | HierarchyException e){
                Logger.error(e);
                guild.getDefaultChannel().sendMessage("ERROR: Please give me `MANAGE_ROLE` permission to use mute command and move the Rubicon Role to the top");
            }
        });
        return muted;
    }

    public static void handleTextChannelCreation(TextChannelCreateEvent event) {
        Role muted = createMutedRoleIfNotExists(event.getGuild());
        if (!event.getGuild().getSelfMember().canInteract(muted))
            event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("I am unable to interact with `rubicon-muted` please give me access").queue();
        TextChannel channel = event.getChannel();
        PermissionOverride override;
        try {
            override = channel.createPermissionOverride(muted).complete();
        } catch (InsufficientPermissionException ex){
            event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("I am unable to handle creation of new channel ` " + event.getChannel().getName() + "`! Please give me `" + ex.getPermission().toString() + "` in order to use mute command").queue();
            return;
        }
        if (override.getDenied().contains(Permission.MESSAGE_WRITE)) return;
        override.getManager().deny(Permission.MESSAGE_WRITE).queue();
    }
}
