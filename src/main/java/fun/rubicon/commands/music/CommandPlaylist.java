package fun.rubicon.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandPlaylist extends CommandHandler {

    public CommandPlaylist() {
        super(new String[]{"playlist"}, CommandCategory.MUSIC, new PermissionRequirements("playlist", false, true), "Lets you save and load music queues.", "save <name>\n" +
                "load <name>\n" +
                "delete <name>\n" +
                "list");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconUser rubiconUser = RubiconUser.fromUser(invocation.getAuthor());
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        if (invocation.getArgs().length == 1) {
            if (!invocation.getArgs()[0].equalsIgnoreCase("list"))
                return createHelpMessage();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Colors.COLOR_PRIMARY);
            embedBuilder.setAuthor(invocation.getAuthor().getName() + " - " + invocation.translate("command.playlist.list.title"), null, invocation.getAuthor().getAvatarUrl());
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<String, List<String>> playlists = rubiconUser.getMusicPlaylists();
            if (playlists == null || playlists.size() == 0)
                return message(error(invocation.translate("command.playlist.no.title"), invocation.translate("command.playlist.no.desc")));
            for (Map.Entry<String, List<String>> entry : playlists.entrySet()) {
                stringBuilder.append(entry.getKey() + String.format(" - %d %s", entry.getValue().size(), invocation.translate("command.playlist.list.tracks")) + "\n");
            }
            embedBuilder.setDescription(stringBuilder.toString());
            return message(embedBuilder);
        }
        GuildMusicPlayer musicPlayer = RubiconBot.getGuildMusicPlayerManager().getAndCreatePlayer(invocation, userPermissions);
        if (invocation.getArgs()[0].equalsIgnoreCase("save")) {
            if (!musicPlayer.checkVoiceAvailability()) {
                return null;
            }
            String name = invocation.getArgs()[1];
            List<AudioTrack> rawQueue = musicPlayer.getTrackList();
            if (musicPlayer.isPlaying()) {
                rawQueue.add(musicPlayer.getPlayingTrack());
            }
            if (rawQueue.size() == 0) {
                return message(error(invocation.translate("command.queue.empty.title"), "command.queue.empty.description"));
            }
            if (!rubiconUser.isPremium())
                if (rubiconUser.getMusicPlaylists() != null)
                    if (rubiconUser.getMusicPlaylists().size() >= 1)
                        return message(noPremium());

            if (rubiconUser.isPremium())
                if (rubiconUser.getMusicPlaylists() != null)
                    if (rubiconUser.getMusicPlaylists().size() >= 10)
                        return message(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.playlist.maximum")));
            List<String> uriQueue = new ArrayList<>();
            for (AudioTrack track : rawQueue) {
                if (track == null)
                    continue;
                uriQueue.add(track.getInfo().uri);
            }
            rubiconUser.saveMusicPlaylist(uriQueue, name);
            return message(success(invocation.translate("command.playlist.saved.title"), String.format(invocation.translate("command.playlist.saved.desc"), name)));
        } else if (invocation.getArgs()[0].equalsIgnoreCase("load")) {
            if (!musicPlayer.checkVoiceAvailability())
                return null;
            String name = invocation.getArgs()[1];
            HashMap<String, List<String>> playlists = rubiconUser.getMusicPlaylists();
            if (playlists == null || playlists.size() == 0)
                return message(error(invocation.translate("command.playlist.no.title"), invocation.translate("command.playlist.no.desc")));
            List<String> tracks = playlists.get(name);
            if (tracks == null)
                return message(error(invocation.translate("command.playlist.nn.title"), invocation.translate("command.playlist.nn.desc")));
            for (String track : tracks) {
                musicPlayer.loadTrack(track, false, true);
            }
            return message(success(invocation.translate("command.playlist.loaded.title"), String.format(invocation.translate("command.playlist.loaded.desc"), name)));
        } else if (invocation.getArgs()[0].equalsIgnoreCase("delete")) {
            String name = invocation.getArgs()[1];
            HashMap<String, List<String>> playlists = rubiconUser.getMusicPlaylists();
            if (!playlists.containsKey(name)) {
                return message(error(invocation.translate("command.playlist.nn.title"), invocation.translate("command.playlist.nn.desc")));
            }
            playlists.remove(name);
            rubiconUser.saveMusicPlaylist(playlists);
            return message(success(invocation.translate("command.playlist.deleted.title"), String.format(invocation.translate("command.playlist.deleted.desc"), name)));
        }
        return createHelpMessage();
    }
}
