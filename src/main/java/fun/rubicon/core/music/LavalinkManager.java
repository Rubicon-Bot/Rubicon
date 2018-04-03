package fun.rubicon.core.music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import lavalink.client.io.Lavalink;
import lavalink.client.io.Link;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavaplayerPlayerWrapper;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class LavalinkManager implements EventListener {

    private final AudioPlayerManager audioPlayerManager;
    private static Lavalink lavalink;

    public LavalinkManager() {
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.enableGcMonitoring();
    }

    public void initialize() {
        ShardManager shardManager = RubiconBot.getShardManager();

        lavalink = new Lavalink(
                shardManager.getApplicationInfo().complete().getId(),
                RubiconBot.getMaximumShardCount(),
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

        try {
            PreparedStatement ps = RubiconBot.getMySQL().prepareStatement("SELECT * FROM lavanodes");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    nodes.add(new LavalinkNode(
                            rs.getString("name"),
                            new URI(rs.getString("uri")),
                            rs.getString("password"))
                    );
                } catch (URISyntaxException e) {
                    Logger.error(e);
                }
            }
        } catch (SQLException e) {
            Logger.error(e);
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
