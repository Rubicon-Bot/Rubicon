package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import fun.rubicon.sql.GuildMusicSQL;
import fun.rubicon.sql.UserMusicSQL;
import fun.rubicon.util.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;

import java.util.*;
import java.util.stream.Collectors;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MusicManager {

    private static List<MusicSearchResult> musicChoose = new ArrayList<>();

    private final GuildMusicSQL guildMusicSQL;
    private final UserMusicSQL userMusicSQL;
    private final Guild guild;
    private final String[] args;
    private final fun.rubicon.permission.UserPermissions userPermissions;
    private final CommandManager.ParsedCommandInvocation parsedCommandInvocation;

    private final String MAINTENANCE_SOUND = "https://lordlee.de/m/maintenance.mp3";
    private final int PLAYLIST_MAXIMUM_DEFAULT = 1;
    private final int PLAYLIST_MAXIMUM_VIP = 5;
    private final int QUEUE_MAXIMUM = 50;
    private final int DEFAULT_VOLUME = 30;
    private final int SKIP_MAXIMUM = 10;

    private final AudioPlayerManager playerManager;
    private static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public MusicManager(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        this.parsedCommandInvocation = parsedCommandInvocation;
        this.guild = parsedCommandInvocation.getGuild();
        this.args = parsedCommandInvocation.getArgs();
        this.userMusicSQL = new UserMusicSQL(parsedCommandInvocation.getAuthor());
        this.guildMusicSQL = new GuildMusicSQL(guild);
        this.userPermissions = new fun.rubicon.permission.UserPermissions(parsedCommandInvocation.getAuthor().getIdLong(), parsedCommandInvocation.getGuild().getIdLong());

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        //playerManager.registerSourceManager(new SoundCloudAudioSourceManager()); //TODO Soundcloud support?
    }

    public Message joinInVoiceChannel() {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        VoiceChannel voiceChannel;
        if (isChannelLockActivated()) {
            voiceChannel = getLockedChannel();
            if (voiceChannel == null)
                return message(error("Error!", "Predefined channel doesn't exist."));
        } else {
            voiceChannel = parsedCommandInvocation.getMember().getVoiceState().getChannel();
            if (isBotInVoiceChannel()) {
                if (getBotsVoiceChannel() == voiceChannel)
                    return message(error("Error!", "Bot is already in your voice channel."));
            }
        }
        guild.getAudioManager().setSendingHandler(getMusicManager(guild).getSendHandler());
        try {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                return message(error("Error!", "I need the VOICE_CONNECT permissions to join a channel."));
            }
        }
        guild.getAudioManager().setSelfDeafened(true);
        return EmbedUtil.message(success("Joined channel", "Joined `" + voiceChannel.getName() + "`"));
    }

    public Message leaveVoiceChannel() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));

        guild.getAudioManager().setSendingHandler(null);
        guild.getAudioManager().closeAudioConnection();
        getCurrentMusicManager().getPlayer().destroy();
        musicManagers.remove(guild.getIdLong());
        return EmbedUtil.message(success("Channel Left", "Left the channel.").setColor(Colors.COLOR_NOT_IMPLEMENTED));
    }

    public Message executePause() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        getCurrentMusicManager().getPlayer().setPaused(true);
        return message(success("Paused!", "Successfully paused playing music."));
    }

    public Message executeResume() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        getCurrentMusicManager().getPlayer().setPaused(false);
        return message(success("Resumed!", "Successfully resumed playing music."));
    }

    public Message playMusic(boolean force) {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        if (!isBotInVoiceChannel())
            joinInVoiceChannel();
        AudioPlayer player = getCurrentMusicManager().getPlayer();
        if (player.isPaused()) {
            player.setPaused(false);
        }
        loadSong(force);
        return null;
    }

    public Message executeVolume() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        String userVolume = "";
        if (parsedCommandInvocation.getArgs().length == 1) {
            userVolume = parsedCommandInvocation.getArgs()[0];
        } else {
            return null;
        }
        int userVolI = Integer.parseInt(userVolume);
        if (userVolI < 1) {
            return message(EmbedUtil.error("Error!", "Volume must be a minimum of 1."));
        }
        if (userVolI > 200) {
            return message(EmbedUtil.error("Error!", "Volume must be 200 or less."));
        }
        getCurrentMusicManager().getPlayer().setVolume(userVolI);
        return message(success("Set volume!", "Successfully changed the volume."));
    }

    public void loadSong(boolean force) {
        TextChannel textChannel = parsedCommandInvocation.getMessage().getTextChannel();
        boolean isURL = false;
        StringBuilder searchParam = new StringBuilder();
        for (int i = 0; i < args.length; i++)
            searchParam.append(args[i]);
        if (searchParam.toString().startsWith("http://") || searchParam.toString().startsWith("https://"))
            isURL = true;

        if (!isURL)
            searchParam.insert(0, "ytsearch: ");

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final boolean isURLFinal = isURL;
        playerManager.loadItemOrdered(getCurrentMusicManager(), searchParam.toString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                String trackName = audioTrack.getInfo().title;
                String trackAuthor = audioTrack.getInfo().author;
                String trackURL = audioTrack.getInfo().uri;
                boolean isStream = audioTrack.getInfo().isStream;
                long trackDuration = audioTrack.getDuration();

                if (!force) {
                    getCurrentMusicManager().getScheduler().queue(audioTrack);
                    embedBuilder.setAuthor("Added a new song to queue", trackURL, null);
                    embedBuilder.addField("Title", trackName, true);
                    embedBuilder.addField("Author", trackAuthor, true);
                    embedBuilder.addField("Duration", (isStream) ? "Stream" : getTimestamp(trackDuration), false);
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                List<AudioTrack> playlistTracks = audioPlaylist.getTracks();
                playlistTracks = playlistTracks.stream().limit(QUEUE_MAXIMUM - getCurrentMusicManager().getScheduler().getQueue().size()).collect(Collectors.toList());

                if (firstTrack == null)
                    firstTrack = playlistTracks.get(0);
                if (force) {
                    getCurrentMusicManager().getPlayer().playTrack(firstTrack);
                    return;
                }
                if (isURLFinal) {
                    playlistTracks.forEach(getCurrentMusicManager().getScheduler()::queue);

                    embedBuilder.setTitle("Added playlist to queue");
                    embedBuilder.setDescription("Added `" + playlistTracks.size() + "` songs from `" + audioPlaylist.getName() + "` to queue.\n" +
                            "\n" +
                            "**Now playing** `" + firstTrack.getInfo().title + "`");
                    embedBuilder.addField("Author", firstTrack.getInfo().author, true);
                    embedBuilder.addField("Duration", (firstTrack.getInfo().isStream) ? "Stream" : getTimestamp(firstTrack.getDuration()), false);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                } else {
                    MusicSearchResult musicSearchResult = new MusicSearchResult(parsedCommandInvocation.getAuthor(), guild, getCurrentMusicManager());
                    audioPlaylist.getTracks().stream().limit(5).collect(Collectors.toList()).forEach(track -> {
                        try {
                            musicSearchResult.addTrack(track);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    musicSearchResult.setMessage(textChannel.sendMessage(musicSearchResult.generateEmbed().build()).complete());
                    musicChoose.add(musicSearchResult);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (musicChoose.contains(musicSearchResult)) {
                                musicSearchResult.getMessage().delete().queue();
                                musicChoose.remove(musicSearchResult);
                            }
                        }
                    }, 15000);
                }
            }

            @Override
            public void noMatches() {
                embedBuilder.setTitle("No matches!");
                embedBuilder.setDescription("There are no matches.");
                embedBuilder.setColor(Colors.COLOR_NOT_IMPLEMENTED);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                embedBuilder.setTitle(":warning: Error!");
                embedBuilder.setDescription("Could not play this song: " + e.getMessage());
                embedBuilder.setColor(Colors.COLOR_ERROR);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }
        });
    }

    public void maintenanceSound() {
        for (Map.Entry entry : musicManagers.entrySet()) {
            playerManager.loadItemOrdered(getCurrentMusicManager(), MAINTENANCE_SOUND, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    ((GuildMusicManager) entry.getValue()).getPlayer().playTrack(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    //DO NOTHING
                }

                @Override
                public void noMatches() {
                    Logger.error("Can't find maintenance sound.");
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    Logger.error("Can't load maintenance sound.");
                }
            });
        }
    }

    public Message executeShuffle() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));

        getCurrentMusicManager().getScheduler().shuffle();
        return message(success("Shuffled!", "Successfully shuffled queue."));
    }

    public Message executeSkip() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        int amount = 1;
        if (parsedCommandInvocation.getArgs().length == 1) {
            if (StringUtil.isNumeric(parsedCommandInvocation.getArgs()[0])) {
                amount = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
            }
        }
        if (amount > SKIP_MAXIMUM)
            return message(error("Error!", "You can only skip " + SKIP_MAXIMUM + " tracks."));
        skipTrack(amount);
        return message(success("Skipped!", "Successfully skipped " + amount + " tracks."));
    }

    public Message executeNowPlaying() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        if (getCurrentMusicManager().getPlayer().getPlayingTrack() == null) {
            return message(error("Error!", "Bot is playing nothing."));
        }
        AudioTrack track = getCurrentMusicManager().getPlayer().getPlayingTrack();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Currently playing", track.getInfo().uri, null);
        embedBuilder.addField("Title", track.getInfo().title, true);
        embedBuilder.addField("Author", track.getInfo().author, true);
        embedBuilder.addField("Duration", (track.getInfo().isStream) ? "Stream" : getTimestamp(track.getDuration()), false);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        parsedCommandInvocation.getMessage().getTextChannel().sendMessage(embedBuilder.build()).queue();
        return null;
    }

    public void skipTrack(int x) {
        for (int i = 0; i < x; i++) {
            getCurrentMusicManager().getScheduler().nextTrack();
        }
    }

    public Message sendQueue() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setTitle("Queue");

        StringBuilder content = new StringBuilder();
        for (AudioTrack track : getCurrentMusicManager().getScheduler().getQueue()) {
            content.append(":small_orange_diamond: [" + track.getInfo().title + "](" + track.getInfo().uri + ")\n");
        }
        builder.setDescription(content.toString());
        parsedCommandInvocation.getMessage().getTextChannel().sendMessage(builder.build()).queue();
        return null;
    }

    public static void handleTrackChoose(MessageReceivedEvent event) {
        List<MusicSearchResult> storage = musicChoose.stream().filter(musicSearchResult -> musicSearchResult.getUser() == event.getAuthor()).collect(Collectors.toList());
        if (storage.size() == 0) {
            return;
        }
        String response = event.getMessage().getContentDisplay();
        if (!StringUtil.isNumeric(response)) {
            return;
        }
        int ans = Integer.parseInt(response);
        if (ans < 1 || ans > 5) {
            return;
        }
        ans--;
        AudioTrack track = storage.get(0).getTrack(ans);
        storage.get(0).getMusicManager().getScheduler().queue(track);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("Added a new song to queue", track.getInfo().uri, null);
        embedBuilder.addField("Title", track.getInfo().title, true);
        embedBuilder.addField("Author", track.getInfo().author, true);
        embedBuilder.addField("Duration", (track.getInfo().isStream) ? "Stream" : getTimestamp(track.getDuration()), false);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        event.getTextChannel().sendMessage(embedBuilder.build()).queue();
        storage.get(0).getMessage().delete().queue();
        musicChoose.remove(storage.get(0));
        event.getMessage().delete().queue();
    }

    private boolean isMemberInVoiceChannel() {
        if (parsedCommandInvocation.getMember().getVoiceState().inVoiceChannel())
            return true;
        return false;
    }

    private boolean isBotInVoiceChannel() {
        if (guild.getSelfMember().getVoiceState().inVoiceChannel())
            return true;
        return false;
    }

    private VoiceChannel getBotsVoiceChannel() {
        if (!isBotInVoiceChannel())
            return null;
        return guild.getSelfMember().getVoiceState().getChannel();
    }

    private boolean isDJ(Member member) {
        Role role = getDJRole();
        if (role == null)
            return true;
        if (member.getRoles().contains(role))
            return true;
        return false;
    }

    private boolean isDJEnabled() {
        if (guildMusicSQL.get("dj").equalsIgnoreCase("false"))
            return false;
        return true;
    }

    private boolean isChannelLockActivated() {
        if (guildMusicSQL.get("locked_channel").equalsIgnoreCase("false"))
            return false;
        return true;
    }

    private VoiceChannel getLockedChannel() {
        if (!isChannelLockActivated())
            return null;
        String entry = guildMusicSQL.get("locked_channel");
        try {
            VoiceChannel channel = RubiconBot.getJDA().getVoiceChannelById(entry);
            return channel;
        } catch (NullPointerException ignored) {

        }
        return null;
    }

    private Role getDJRole() {
        if (!isDJEnabled())
            return null;
        String entry = guildMusicSQL.get("guildid");
        try {
            Role role = RubiconBot.getJDA().getRoleById(entry);
            return role;
        } catch (NullPointerException ignored) {

        }
        return null;
    }

    private GuildMusicManager getMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);
        if (musicManager == null) {
            synchronized (musicManagers) {
                musicManager = musicManagers.get(guildId);
                if (musicManager == null) {
                    musicManager = new GuildMusicManager(playerManager);
                    musicManager.getPlayer().setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, musicManager);
                }
            }
        }
        return musicManager;
    }

    private GuildMusicManager getCurrentMusicManager() {
        return getMusicManager(parsedCommandInvocation.getGuild());
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    private AudioTrack getCurrentTrack() {
        MusicManager manager = this;
        return manager.getCurrentMusicManager().getScheduler().getPlayer().getPlayingTrack();
    }

    public Message executeLyrics() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        if (getCurrentMusicManager().getPlayer().getPlayingTrack() == null) {
            return message(error("Error!", "Bot is playing nothing."));
        }
        MusixMatch musixMatch = new MusixMatch(Info.MUSIXMATCH_KEY);
        AudioTrackInfo info = this.getCurrentTrack().getInfo();
        Track track;
        Lyrics lyrics;
        try {
            track = musixMatch.getMatchingTrack(info.title, info.author);
            lyrics = musixMatch.getLyrics(track.getTrack().getTrackId());
        } catch (MusixMatchException e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No lyrics found", "There are no lyrics of the current song on Musixmatch").build()).build();
        }
        EmbedBuilder lyricsEmbed = new EmbedBuilder();
        lyricsEmbed.setColor(Colors.COLOR_PREMIUM);
        lyricsEmbed.setTitle("Lyrics of `" + track.getTrack().getTrackName() + "`", track.getTrack().getTrackShareUrl());
        lyricsEmbed.setFooter(lyrics.getLyricsCopyright(), null);
        lyricsEmbed.setDescription(lyrics.getLyricsBody());
        return new MessageBuilder().setEmbed(lyricsEmbed.build()).build();
    }

    public Map<Long, GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }
}
