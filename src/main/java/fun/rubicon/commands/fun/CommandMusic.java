package fun.rubicon.commands.fun;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.GuildMusicManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.sql.GuildMusicSQL;
import fun.rubicon.sql.UserMusicSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandMusic extends CommandHandler {

    private GuildMusicSQL guildMusicSQL;
    private UserMusicSQL userMusicSQL;
    private Guild guild;
    private String[] args;
    private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

    private final int PLAYLIST_MAXIMUM_DEFAULT = 1;
    private final int PLAYLIST_MAXIMUM_VIP = 5;
    private final int QUEUE_MAXIMUM = 50;
    private final int DEFAULT_VOLUME = 25;

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;

    //TODO Parameter Usage
    public CommandMusic() {
        super(new String[]{"music", "m"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.music"), "Chill with your friends and listen to music.", "");

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        //playerManager.registerSourceManager(new SoundCloudAudioSourceManager()); //TODO Soundcloud support?

        musicManagers = new HashMap<>();
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        this.parsedCommandInvocation = parsedCommandInvocation;
        this.guild = parsedCommandInvocation.invocationMessage.getGuild();
        this.args = parsedCommandInvocation.args;
        this.userMusicSQL = new UserMusicSQL(parsedCommandInvocation.invocationMessage.getAuthor());
        this.guildMusicSQL = new GuildMusicSQL(guild);
        if (args.length == 0) {
            return createHelpMessage();
        } else if (args.length == 1) {
            switch (args[0]) {
                case "join":
                case "summon":
                case "start":
                    return joinInVoiceChannel();

                case "stop":
                case "leave":
                    return leaveVoiceChannel();
            }
        } else if (args.length >= 2)
            switch (args[0]) {
                case "play":
                    return playMusic();
            }
        return createHelpMessage();
    }

    private Message joinInVoiceChannel() {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        VoiceChannel voiceChannel;
        if (isChannelLockActivated()) {
            voiceChannel = getLockedChannel();
            if (voiceChannel == null)
                return message(error("Error!", "Predefined channel doesn't exist."));
        } else {
            voiceChannel = parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel();
            if(isBotInVoiceChannel()) {
                if(getBotsVoiceChannel() == voiceChannel)
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
        return new MessageBuilder(":inbox_tray: Joined `" + voiceChannel.getName() + "`").build();
    }

    private Message leaveVoiceChannel() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));

        guild.getAudioManager().setSendingHandler(null);
        guild.getAudioManager().closeAudioConnection();
        getCurrentMusicManager().getPlayer().destroy();
        return new MessageBuilder(":outbox_tray: Left the voice channel").build();
    }

    private Message playMusic() {
        if (!isMemberInVoiceChannel())
            return message(error("Error!", "To use this command you have to be in a voice channel."));
        if (!isBotInVoiceChannel())
            joinInVoiceChannel();
        AudioPlayer player = getCurrentMusicManager().getPlayer();
        if (player.isPaused()) {
            player.setPaused(false);
        }
        loadSong();
        return null;
    }

    private void loadSong() {
        TextChannel textChannel = parsedCommandInvocation.invocationMessage.getTextChannel();
        boolean isURL = false;
        StringBuilder searchParam = new StringBuilder();
        for (int i = 1; i < args.length; i++)
            searchParam.append(args[i]);
        if (searchParam.toString().startsWith("http://") || searchParam.toString().startsWith("https://"))
            isURL = true;

        //TODO Remove this later
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

                getCurrentMusicManager().getScheduler().queue(audioTrack);

                embedBuilder.setAuthor("Added a new song to queue", trackURL, null);
                embedBuilder.addField("Title", trackName, true);
                embedBuilder.addField("Author", trackAuthor, true);
                embedBuilder.addField("Duration", (isStream) ? "Stream" : getTimestamp(trackDuration), false);
                embedBuilder.setThumbnail(trackURL);
                embedBuilder.setColor(Colors.COLOR_PRIMARY);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
                List<AudioTrack> playlistTracks = audioPlaylist.getTracks();
                playlistTracks = playlistTracks.stream().limit(QUEUE_MAXIMUM).collect(Collectors.toList());

                if (firstTrack == null)
                    firstTrack = playlistTracks.get(0);
                if (isURLFinal) {
                    playlistTracks.forEach(getCurrentMusicManager().getScheduler()::queue);

                    embedBuilder.setTitle("Added playlist to queue");
                    embedBuilder.setDescription("Added `" + playlistTracks.size() + "` songs from `" + audioPlaylist.getName() + "` to queue.\n" +
                            "\n" +
                            "**Now playing** `" + firstTrack.getInfo().title + "`");
                    embedBuilder.addField("Author", firstTrack.getInfo().author, true);
                    embedBuilder.addField("Duration", (firstTrack.getInfo().isStream) ? "Stream" : getTimestamp(firstTrack.getDuration()), false);
                    embedBuilder.setThumbnail(firstTrack.getInfo().uri);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                } else {
                    getCurrentMusicManager().getScheduler().queue(firstTrack);
                    embedBuilder.setAuthor("Added a new song to queue", firstTrack.getInfo().uri, null);
                    embedBuilder.addField("Title", firstTrack.getInfo().title, true);
                    embedBuilder.addField("Author", firstTrack.getInfo().author, true);
                    embedBuilder.addField("Duration", (firstTrack.getInfo().isStream) ? "Stream" : getTimestamp(firstTrack.getDuration()), false);
                    embedBuilder.setThumbnail(firstTrack.getInfo().uri);
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    textChannel.sendMessage(embedBuilder.build()).queue();
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

    public static void handleReactions(MessageReactionAddEvent event) {

    }

    private boolean isMemberInVoiceChannel() {
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().inVoiceChannel())
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
            return true; //TODO or false?
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
        String guildId = guild.getId();
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
        return getMusicManager(parsedCommandInvocation.invocationMessage.getGuild());
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
}
