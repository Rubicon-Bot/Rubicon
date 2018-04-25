package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fun.rubicon.command.CommandManager;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MusicSearchResult {

    private final CommandManager.ParsedCommandInvocation invocation;
    private final User user;
    private final Guild guild;
    private final List<AudioTrack> trackList;
    private final GuildMusicPlayer guildMusicPlayer;
    private final boolean force;
    private Message chooseMessage;

    private final String[] emotes = {
            ":one:",
            ":two:",
            ":three:",
            ":four:",
            ":five:"
    };

    public MusicSearchResult(CommandManager.ParsedCommandInvocation invocation, GuildMusicPlayer guildMusicPlayer, boolean force) {
        this.invocation = invocation;
        this.guildMusicPlayer = guildMusicPlayer;
        this.force = force;
        this.user = invocation.getAuthor();
        this.guild = invocation.getGuild();

        trackList = new ArrayList<>();
    }

    public void addTrack(AudioTrack track) throws Exception {
        if (trackList.size() >= 5)
            throw new Exception("No support for more than 5 links.");
        trackList.add(track);
    }

    public EmbedBuilder generateEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        int i = 0;
        StringBuilder description = new StringBuilder();
        for (AudioTrack track : trackList) {
            description.append(emotes[i]).append("  [").append(track.getInfo().title).append("](").append(track.getInfo().uri).append(")\n\n");
            i++;
        }
        builder.setFooter(invocation.translate("command.play.choose"), null);
        builder.setDescription(description.toString());
        return builder;
    }

    public AudioTrack getTrack(int index) {
        return trackList.get(index);
    }

    public void setMessage(Message message) {
        this.chooseMessage = message;
    }

    public Message getMessage() {
        return chooseMessage;
    }

    public User getUser() {
        return user;
    }

    public Guild getGuild() {
        return guild;
    }

    public GuildMusicPlayer getGuildMusicPlayer() {
        return guildMusicPlayer;
    }

    public boolean isForce() {
        return force;
    }
}