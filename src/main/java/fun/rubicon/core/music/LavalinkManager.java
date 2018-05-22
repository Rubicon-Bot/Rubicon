package fun.rubicon.core.music;


import com.rethinkdb.net.Cursor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import lavalink.client.io.Lavalink;
import lavalink.client.io.Link;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class LavalinkManager implements EventListener {

    private final AudioPlayerManager audioPlayerManager;
    private static Lavalink lavalink;

    public LavalinkManager() {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioPlayerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
    }

    public void initialize() {
        ShardManager shardManager = RubiconBot.getShardManager();

        lavalink = new Lavalink(
                shardManager.getApplicationInfo().complete().getId(),
                Integer.parseInt(RubiconBot.getConfiguration().getString("shard_count")),
                shardManager::getShardById
        );

        loadNodes().forEach(lavalinkNode -> lavalink.addNode(lavalinkNode.getName(), lavalinkNode.getUri(), lavalinkNode.getPassword()));
    }


    public IPlayer getPlayer(String guildId) {
        IPlayer player = getLink(guildId).getPlayer();
        return player == null ? new LavaplayerPlayerWrapper(audioPlayerManager.createPlayer()) : player;
    }

    public Link getLink(String guildId) {
        return lavalink.getLink(guildId);
    }

    //Connection Handling
    public void createConnection(VoiceChannel voiceChannel) {
        getLink(voiceChannel.getGuild().getId()).connect(voiceChannel);
    }

    public void closeConnection(String guildId) {
        getLink(guildId).disconnect();
    }

    public boolean isConnected(String guildId) {
        VoiceChannel channel = getLink(guildId).getChannel();
        return channel != null;
    }

    private List<LavalinkNode> loadNodes() {
        List<LavalinkNode> nodes = new ArrayList<>();

        Cursor cursor = RubiconBot.getRethink().db.table("lavanodes").run(RubiconBot.getRethink().getConnection());
        for (Object obj : cursor) {
            Map map = (Map) obj;
            try {
                nodes.add(new LavalinkNode(
                        (String) map.get("name"),
                        new URI((String) map.get("uri")),
                        (String) map.get("password"))
                );
            } catch (URISyntaxException e) {
                Logger.error(e);
            }
        }


        if (nodes.isEmpty()) {
            throw new RuntimeException("No lavalink nodes provided.");
        }
        Logger.info(String.format("Loaded %d lavalink nodes", nodes.size()));
        return nodes;
    }

    @Override
    public void onEvent(Event event) {
        if (lavalink != null) {
            lavalink.onEvent(event);
        }
    }

    public Lavalink getLavalink() {
        return lavalink;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }
}
