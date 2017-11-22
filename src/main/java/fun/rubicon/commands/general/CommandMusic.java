package fun.rubicon.commands.general;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.music.Manager;
import fun.rubicon.core.music.PlayerSendHandler;
import fun.rubicon.core.music.Track;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.xml.soap.Text;
import java.awt.Color;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by zekro on 22.11.2017 / 15:18
 * rubiconBot.fun.rubicon.commands.general
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */

public class CommandMusic extends Command {

    private static AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private static HashMap<Guild, Map.Entry<AudioPlayer, Manager>> players = new HashMap<>();
    private static HashMap<Guild, Message> playerMsgs = new HashMap<>();
    private static Guild guild;
    private TextChannel chan;

    private static final int PLAYLIST_LIMIT = 5000;


    public CommandMusic(String command, CommandCategory category) {
        super(command, category);
        AudioSourceManagers.registerRemoteSources(manager);
    }


    private Message error(String msg) {
        return chan.sendMessage(new EmbedBuilder().setColor(Color.red).setDescription(msg).build()).complete();
    }


    private AudioPlayer createPlayer() {
        AudioPlayer p = manager.createPlayer();
        Manager m = new Manager(p);
        p.addListener(m);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(p));
        players.put(guild, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }


    private Manager getManager() {
        return players.get(guild).getValue();
    }

    private AudioPlayer getPlayer() {
        if (players.containsKey(guild)) {
            return players.get(guild).getKey();
        } else {
            return createPlayer();
        }
    }


    private void createPlayerMessage() {
        chan.sendMessage(createPlayerEmbed(getPlayer().getPlayingTrack())).queue(
                m -> playerMsgs.put(guild, m)
        );
    }

    private MessageEmbed createPlayerEmbed(AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        return new EmbedBuilder()
                .setTitle("PLAYER")
                .setColor(Colors.COLOR_PRIMARY)
                .setDescription(
                        info.title + " by " + info.author
                )
                .build();
    }


    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

        chan = e.getTextChannel();
        guild = e.getGuild();
        Member author =e.getMember();

        if (args.length < 1) {
            error(getUsage());
            return;
        }

        switch (args[0]) {
            case "play":
                if (args.length < 2) {
                    error("Please enter a valid play query!");
                    return;
                }
                if (playerMsgs.containsKey(guild))
                    try {
                        playerMsgs.get(guild).delete().queue();
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                else {

                    String input = Arrays.stream(args).skip(1).map(s -> " " + s).collect(Collectors.joining()).substring(1);
                    if (!(input.startsWith("http://") || input.startsWith("https://")))
                        input = "ytsearch: " + input;

                    getPlayer();

                    manager.setFrameBufferDuration(5000);
                    manager.loadItemOrdered(guild, input, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            getManager().queue(track, author);
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            for (int i = 0; i < (playlist.getTracks().size() > PLAYLIST_LIMIT ? PLAYLIST_LIMIT : playlist.getTracks().size()); i++) {
                                getManager().queue(playlist.getTracks().get(i), author);
                            }
                        }

                        @Override
                        public void noMatches() {

                        }

                        @Override
                        public void loadFailed(FriendlyException exception) {
                            exception.printStackTrace();
                        }
                    });

                    createPlayerMessage();
                }

                break;
        }

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
