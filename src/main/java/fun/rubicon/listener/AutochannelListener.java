/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class AutochannelListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        try {
            VoiceChannel voiceChannel = event.getChannelJoined();
            if (isAutochannel(event.getGuild(), voiceChannel.getId())) {
                if (hasPermissions(event.getGuild())) {
                    VoiceChannel newChannel = (VoiceChannel) event.getGuild().getController().createCopyOfChannel(voiceChannel).setName(voiceChannel.getName() + " [AC]").complete();
                    event.getGuild().getController().moveVoiceMember(event.getMember(), newChannel).queue();
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            VoiceChannel voiceChannel = event.getChannelJoined();
            if (isAutochannel(event.getGuild(), voiceChannel.getId())) {
                if (hasPermissions(event.getGuild())) {
                    VoiceChannel newChannel = (VoiceChannel) event.getGuild().getController().createCopyOfChannel(voiceChannel).setName(voiceChannel.getName() + " [AC]").complete();
                    event.getGuild().getController().moveVoiceMember(event.getMember(), newChannel).queue();
                }
            }
            if (event.getChannelLeft().getMembers().isEmpty()) {
                if (event.getChannelLeft().getName().contains("[AC]")) {
                    if (hasPermissions(event.getGuild())) {
                        event.getChannelLeft().delete().queue();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        try {
            if (event.getChannelLeft().getMembers().isEmpty()) {
                if (event.getChannelLeft().getName().contains("[AC]")) {
                    if (hasPermissions(event.getGuild())) {
                        event.getChannelLeft().delete().queue();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    private boolean hasPermissions(Guild guild) {
        Member selfMember = guild.getSelfMember();
        return selfMember.getPermissions().contains(Permission.MANAGE_CHANNEL);
    }

    private boolean isAutochannel(Guild guild, String channelId) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(guild);
        return rubiconGuild.isAutochannel(channelId);
    }
}
