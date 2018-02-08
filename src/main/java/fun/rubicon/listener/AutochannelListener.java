package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */

public class AutochannelListener extends ListenerAdapter {

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
        String oldEntry = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        if (oldEntry != null)
            if (oldEntry.contains(e.getChannel().getId())) {
                String newEntry = oldEntry.replace(e.getChannel().getId() + ",", "");
                RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
            }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if (isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if (isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
        if (e.getChannelLeft().getMembers().size() == 0) {
            if (e.getChannelLeft().getName().contains("[AC]")) {
                e.getChannelLeft().delete().queue();
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if (e.getChannelLeft().getMembers().size() == 0) {
            if (e.getChannelLeft().getName().contains("[AC]")) {
                e.getChannelLeft().delete().queue();
            }
        }
    }

    private boolean isAutoChannel(Guild g, Channel ch) {
        String oldEntry = RubiconBot.getMySQL().getGuildValue(g, "autochannels");
        if (oldEntry != null)
            if (oldEntry.contains(ch.getId())) {
                return true;
            }
        return false;
    }
}
