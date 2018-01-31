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
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

import java.sql.SQLException;

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
        Member user = parsedCommandInvocation.getMember();
        Guild guild = parsedCommandInvocation.getGuild();

        if(message.getMentionedUsers().isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "mute <@User>").build()).build();
        Member victim = guild.getMember(message.getMentionedUsers().get(0));
        if(!user.canInteract(victim))
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "You have no permission to interact with " + victim.getAsMention()).build()).build();
        Role muted = createMutedRoleIfNotExists(guild);
        if(victim.getRoles().contains(muted))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Already muted", "This user is already muted").build()).build();
        guild.getController().addSingleRoleToMember(victim, muted).queue();
        return new MessageBuilder().setEmbed(EmbedUtil.success("Muted", "Successfully muted " + victim.getAsMention()).build()).build();
    }

    public static Role createMutedRoleIfNotExists(Guild guild){
        if(!guild.getRolesByName("rubicon-muted", false).isEmpty()) return guild.getRolesByName("rubicon-muted", false).get(0);
        Role muted = null;
        try{
            muted = guild.getController().createRole().setName("rubicon-muted").complete();
            guild.getRoles().get(0).delete().queue();
        } catch (InsufficientPermissionException | HierarchyException e){
            guild.getDefaultChannel().sendMessage("ERROR: Please give me MANAGE_ROLE permission to use mute command and move the Rubicon Role to the top");
        }
        Role finalMuted = muted;
        guild.getTextChannels().forEach(c -> {
            PermissionOverride override = c.createPermissionOverride(finalMuted).complete();
            if(override.getDenied().contains(Permission.MESSAGE_WRITE)) return;
            override.getManager().deny(Permission.MESSAGE_WRITE).queue();
        });
        return muted;
    }

    public static void handleTextChannelCreation(TextChannelCreateEvent event){
        Role muted = createMutedRoleIfNotExists(event.getGuild());
        if(!event.getGuild().getSelfMember().canInteract(muted))
            event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage("I am unable to interact with `rubicon-muted` please give me access").queue();
        TextChannel channel = event.getChannel();
        PermissionOverride override = channel.createPermissionOverride(muted).complete();
        if(override.getDenied().contains(Permission.MESSAGE_WRITE)) return;
        override.getManager().deny(Permission.MESSAGE_WRITE).queue();
    }
}
