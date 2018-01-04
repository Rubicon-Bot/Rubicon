package fun.rubicon.commands.fun;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
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
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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
    private final int DEFAULT_VOLUME = 50;

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;

    //TODO Parameter Usage
    public CommandMusic() {
        super(new String[]{"music", "m"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.music"), "Chill with your friends and listen to music.", "");

        playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());

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
            Logger.debug("So Something");
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
        } else
            voiceChannel = parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel();
        guild.getAudioManager().setSendingHandler(getMusicManager(guild).getSendHandler());
        try {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        } catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                return message(error("Error!", "I need the VOICE_CONNECT permissions to join a channel."));
            }
        }
        return message(success("Success!", "Joined the `" + voiceChannel.getName() + "` channel."));
    }

    private Message leaveVoiceChannel() {
        if (!isBotInVoiceChannel())
            return message(error("Error!", "Bot is not in a voice channel."));
        VoiceChannel channel = getBotsVoiceChannel();
        if (parsedCommandInvocation.invocationMessage.getMember().getVoiceState().getChannel() != channel)
            return message(error("Error!", "You have to be in the same voice channel as the bot."));
        guild.getAudioManager().setSendingHandler(null);
        guild.getAudioManager().closeAudioConnection();
        return message(success("Success!", "Bot left the channel."));
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
