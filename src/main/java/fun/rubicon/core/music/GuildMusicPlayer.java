package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fun.rubicon.RubiconBot;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;

import java.util.Queue;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class GuildMusicPlayer extends AudioEventAdapterWrapped implements AudioSendHandler {

    private final Guild guild;
    private final AudioPlayerManager audioPlayerManager;
    private final IPlayer player;
    private final Queue<AudioTrack> trackQueue;

    public GuildMusicPlayer(Guild guild) {
        this.guild = guild;
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.player = RubiconBot.getLavalinkManager().getPlayer(guild.getId());
        this.trackQueue = trackQueue;
    }
}
