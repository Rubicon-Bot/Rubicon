package fun.rubicon.listener;

import fun.rubicon.core.Main;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class AutochannelListener extends ListenerAdapter {

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent e) {
        String oldEntry = Main.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        if(oldEntry.contains(e.getChannel().getId())) {
            String newEntry = oldEntry.replace(e.getChannel().getId() + ",", "");
            Main.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if(isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
        VoiceChannel ch = e.getChannelJoined();
        if(isAutoChannel(e.getGuild(), ch)) {
            VoiceChannel newChannel = (VoiceChannel) e.getGuild().getController().createCopyOfChannel(ch).setName(ch.getName() + " [AC]").complete();
            e.getGuild().getController().moveVoiceMember(e.getMember(), newChannel).queue();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
    }

    private boolean isAutoChannel(Guild g, Channel ch) {
        String oldEntry = Main.getMySQL().getGuildValue(g, "autochannels");
        if(oldEntry.contains(ch.getId())) {
            return true;
        }
        return false;
    }
}
