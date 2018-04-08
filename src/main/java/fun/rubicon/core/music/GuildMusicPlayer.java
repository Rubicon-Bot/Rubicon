package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.music.QueueMessage;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.*;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class GuildMusicPlayer extends MusicPlayer {

    /* TODO
     * - add silent option
     */

    private static List<MusicSearchResult> musicChoose = new ArrayList<>();
    public static Map<Long, Queue<AudioTrack>> queueStorage = new HashMap<>();

    private final CommandManager.ParsedCommandInvocation invocation;
    private final UserPermissions userPermissions;
    private final Guild guild;
    private final IPlayer player;
    private final AudioManager audioManager;
    private final AudioPlayerManager audioPlayerManager;

    private final GuildMusicPlayer instance;

    public GuildMusicPlayer(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        this.invocation = invocation;
        this.userPermissions = userPermissions;
        this.guild = invocation.getGuild();
        this.player = lavalinkManager.getPlayer(guild.getId());
        this.audioManager = guild.getAudioManager();
        this.audioPlayerManager = lavalinkManager.getAudioPlayerManager();

        if (queueStorage.get(guild.getIdLong()) == null)
            setQueue(new LinkedList<>());
        else
            setQueue(queueStorage.get(guild.getIdLong()));

        initMusicPlayer(guild, player);
        instance = this;
    }

    public void join() {
        if (!invocation.getMember().getVoiceState().inVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!RubiconBot.getLavalinkManager().isConnected(invocation.getGuild().getId())) {
            joinChannel();
            return;
        }
        PermissionRequirements moveIfInVoiceChannelPermissions = new PermissionRequirements("join.move", false, true);

        if (!moveIfInVoiceChannelPermissions.coveredBy(userPermissions)) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.no_permissions()));
            return;
        }
        joinChannel();
    }

    private void joinChannel() {
        if (!invocation.getSelfMember().hasPermission(invocation.getMember().getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.join.bot.notconnectperms."), invocation.translate("command.join.bot.notconnectperms.description"))));
            return;
        }
        RubiconBot.getLavalinkManager().createConnection(invocation.getMember().getVoiceState().getChannel());
        audioManager.setSelfDeafened(true);
        setVolume(DEFAULT_VOLUME);
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.join.joined.title"), invocation.translate("command.join.joined.description").replace("%channel%", invocation.getMember().getVoiceState().getChannel().getName()))));
    }


    public void leave() {
        GuildVoiceState voiceState = invocation.getMember().getVoiceState();
        VoiceChannel botChannel = RubiconBot.getLavalinkManager().getLink(invocation.getGuild().getId()).getChannel();
        if (botChannel == null) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return;
        }
        if (!voiceState.inVoiceChannel() || voiceState.getChannel() != botChannel) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.nosame.title"), invocation.translate("command.leave.nosame.title"))));
            return;
        }
        closeAudioConnection();
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.left.title"), invocation.translate("command.leave.left.title"))));
    }

    public void playMusic(boolean force) {
        if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!isBotInVoiceChannel()) {
            joinChannel();
        }
        if (player.isPaused()) {
            resume();
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("phrase.resumed.title"), invocation.translate("phrase.resumed.titile"))));
            return;
        }
        loadTrack(force);
    }

    public void forcePlay() {
        playMusic(true);
    }

    public void loadTrack(boolean forcePlay) {
        String keyword = invocation.getArgsString();
        boolean isUrl = true;
        if (!keyword.startsWith("http://") && !keyword.startsWith("https://")) {
            keyword = "ytsearch: " + keyword;
            isUrl = false;
        }
        final boolean isURL = isUrl;
        audioPlayerManager.loadItemOrdered(player.toString(), keyword, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                TrackDataHolder trackData = new TrackDataHolder(track);
                if (forcePlay) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor(invocation.translate("command.play.loadTrack.playing"), trackData.url, null);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.title"), trackData.name, true);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.author"), trackData.author, true);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.duration"), trackData.isStream ? invocation.translate("command.play.loadTrack.stream") : getTimestamp(trackData.duration), false);
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(embedBuilder));
                    play(track);
                } else {
                    queueTrack(track);
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor(forcePlay ? invocation.translate("command.play.loadTrack.playing") : invocation.translate("command.play.loadTrack.queued"), trackData.url, null);
                embedBuilder.addField(invocation.translate("command.play.loadTrack.title"), trackData.name, true);
                embedBuilder.addField(invocation.translate("command.play.loadTrack.author"), trackData.author, true);
                embedBuilder.addField(invocation.translate("command.play.loadTrack.duration"), trackData.isStream ? invocation.translate("command.play.loadTrack.stream") : getTimestamp(trackData.duration), false);
                embedBuilder.setColor(Colors.COLOR_PRIMARY);
                SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(embedBuilder));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> playlistTracks = playlist.getTracks();
                playlistTracks = playlistTracks.stream().limit(QUEUE_MAXIMUM - getQueueSize()).collect(Collectors.toList());

                if (firstTrack == null) {
                    firstTrack = playlistTracks.get(0);
                    playlistTracks.remove(firstTrack);
                }
                if (forcePlay) {
                    TrackDataHolder trackData = new TrackDataHolder(firstTrack);
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor(invocation.translate("command.play.loadTrack.playing"), trackData.url, null);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.title"), trackData.name, true);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.author"), trackData.author, true);
                    embedBuilder.addField(invocation.translate("command.play.loadTrack.duration"), trackData.isStream ? invocation.translate("command.play.loadTrack.stream") : getTimestamp(trackData.duration), false);
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(embedBuilder));
                    play(firstTrack);
                    return;
                }
                if (isURL) {
                    playlistTracks.forEach(track -> queueTrack(track));
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor(invocation.translate("command.play.loadPlaylist.queued.title"), null, null);
                    embedBuilder.setDescription(invocation.translate("command.play.loadPlaylist.queued.description")
                            .replaceFirst("%count%", playlistTracks.size() + "")
                            .replaceFirst("%name%", playlist.getName())
                            .replaceFirst("%sname%", firstTrack.getInfo().title));
                    embedBuilder.addField(invocation.translate("command.play.loadPlaylist.queued.author"), firstTrack.getInfo().author, false);
                    embedBuilder.addField(invocation.translate("command.play.loadPlaylist.queued.duration"), getTimestamp(firstTrack.getDuration()), false);
                    embedBuilder.setColor(Colors.COLOR_PRIMARY);
                    SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(embedBuilder));
                    return;
                }
                MusicSearchResult musicSearchResult = new MusicSearchResult(invocation, instance, forcePlay);
                playlist.getTracks().stream().limit(5).collect(Collectors.toList()).forEach(track -> {
                    try {
                        musicSearchResult.addTrack(track);
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                });
                musicSearchResult.setMessage(SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(musicSearchResult.generateEmbed())));
                musicChoose.add(musicSearchResult);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (musicChoose.contains(musicSearchResult)) {
                            //musicSearchResult.getMessage().delete().queue();
                            musicChoose.remove(musicSearchResult);
                        }
                    }
                }, 15000);
            }

            @Override
            public void noMatches() {
                Logger.debug("3");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Logger.debug("4");
            }
        });
    }

    private boolean isMemberInSameChannel() {
        return invocation.getMember().getVoiceState().getChannel().equals(invocation.getSelfMember().getVoiceState().getChannel());
    }

    private boolean isMemberInVoiceChannel() {
        return invocation.getMember().getVoiceState().inVoiceChannel();
    }

    private boolean isBotInVoiceChannel() {
        return invocation.getSelfMember().getVoiceState().inVoiceChannel();
    }

    @Override
    public void closeAudioConnection() {
        clearQueue();
        queueStorage.replace(guild.getIdLong(), null);
        player.stopTrack();
        lavalinkManager.closeConnection(guild.getId());
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

    public void skip() {
        if (!isBotInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return;
        }
        if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!isMemberInSameChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
            return;
        }
        int count = 1;
        if (invocation.getArgs().length > 0) {
            try {
                count = Integer.parseInt(invocation.getArgs()[0]);
            } catch (NumberFormatException e) {
                SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.skip.invalidnumber.title"), invocation.translate("command.skip.invalidnumber.description"))));
            }
        }
        skipTrack(count);
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.skip.skipped.title"), String.format(invocation.translate("command.skip.skipped.description"), count))));
    }

    public void shuffleUp() {
        if (!isBotInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return;
        }
        if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!isMemberInSameChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
            return;
        }
        shuffle();
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.shuffle.shuffled.title"), invocation.translate("command.shuffle.shuffled.description"))));
    }

    public void clear() {
        if (!isBotInVoiceChannel())
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
        if (!isMemberInVoiceChannel())
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
        if (!isMemberInSameChannel())
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
        clearQueue();
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.clearqueue.cleared.title"), invocation.translate("command.clearqueue.cleared.title"))));
    }

    public void queue() {
        if (getTrackList().isEmpty()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.queue.empty.title"), invocation.translate("command.queue.empty.description"))));
            return;
        }
        try {
            int sideNumb = invocation.getArgs().length > 0 ? Integer.parseInt(invocation.getArgs()[0]) : 1;
            List<String> tracks = new ArrayList<>();
            List<String> tracksSubList;
            getTrackList().forEach(track -> tracks.add(buildQueueEntry(track)));
            int sideNumbAll = tracks.size() >= 20 ? tracks.size() / 20 : 1;
            if (sideNumb > sideNumbAll) {
                SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.queue.invalidpage.title"), invocation.translate("command.queue.invalidpage.description"))));
                return;
            }
            if (tracks.size() > 20)
                tracksSubList = tracks.subList((sideNumb - 1) * 20, (sideNumb - 1) * 20 + 20);
            else
                tracksSubList = tracks;

            String formattedQueue = tracksSubList.stream().collect(Collectors.joining("\n"));
            Message msg = SafeMessage.sendMessageBlocking(invocation.getTextChannel(),
                    new EmbedBuilder().setDescription("**CURRENT QUEUE:**\n" +
                            "*[" + getTrackList().size() + " Tracks | Side " + sideNumb + " / " + sideNumbAll + "]* \n" +
                            formattedQueue
                    ).build());
            if (tracks.size() > 20) {
                msg.addReaction("➡").queue();
                new QueueMessage(sideNumbAll, msg, tracks, invocation.getAuthor());
            }
        } catch (NumberFormatException e) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.queue.invalidnumber.title"), invocation.translate("command.queue.invalidnumber.description"))));
        }
    }

    private String buildQueueEntry(AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        return "`[ " + getTimestamp(info.length) + " ] " + info.title + "`";
    }

    public void pauseMusic() {
        if (!isBotInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return;
        }
        if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!isMemberInSameChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
            return;
        }
        if (player.getPlayingTrack() == null) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.notplayingtrack.title"), invocation.translate("phrase.notplayingtrack.description"))));
            return;
        }
        pause();
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("command.pause.paused.title"), invocation.translate("command.pause.paused.description"))));

    }

    public void resumeMusic() {
        if (!isBotInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return;
        }
        if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return;
        }
        if (!isMemberInSameChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
            return;
        }
        if (!player.isPaused()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.resume.notpaused.title"), invocation.translate("command.resume.notpaused.description"))));
            return;
        }
        resume();
        SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.success(invocation.translate("phrase.resumed.title"), invocation.translate("phrase.resumed.description"))));
    }


    private static class TrackDataHolder {

        public AudioTrack track;
        public String name;
        public String author;
        public String url;
        boolean isStream;
        long duration;

        private TrackDataHolder(AudioTrack track) {
            this.track = track;
            this.name = track.getInfo().title;
            this.author = track.getInfo().author;
            this.url = track.getInfo().uri;
            this.isStream = track.getInfo().isStream;
            this.duration = track.getDuration();
        }
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
        TrackDataHolder trackData = new TrackDataHolder(track);
        storage.get(0).getGuildMusicPlayer().queueTrack(track);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(storage.get((0)).isForce() ? TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.playing") : TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.queued"), trackData.url, null);
        embedBuilder.addField(TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.title"), trackData.name, true);
        embedBuilder.addField(TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.author"), trackData.author, true);
        embedBuilder.addField(TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.duration"), trackData.isStream ? TranslationUtil.translate(event.getAuthor(), "command.play.loadTrack.stream") : getTimestamp(trackData.duration), false);
        embedBuilder.setColor(Colors.COLOR_PRIMARY);
        SafeMessage.sendMessage(event.getTextChannel(), embedBuilder.build());
        storage.get(0).getMessage().delete().queue();
        musicChoose.remove(storage.get(0));
        if (event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_MANAGE))
            event.getMessage().delete().queue();
    }

    public CommandManager.ParsedCommandInvocation getInvocation() {
        return invocation;
    }

    private void skipTrack(int x) {
        if (x > 25) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.skip.toomuch.title"), invocation.translate("command.skip.toomuch.description"))));
            return;
        } else if (x < 0) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.skip.tooless.title"), invocation.translate("command.skip.tooless.description"))));
            return;
        }
        for (int i = 1; i < x; i++) {
            pollTrack();
            if (getQueueSize() == 0)
                break;
        }
        play(pollTrack());
    }

    public boolean checkVoiceAvailability() {
        if (!isBotInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("command.leave.novc.title"), invocation.translate("command.leave.novc.description"))));
            return false;
        } else if (!isMemberInVoiceChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.novc.title"), invocation.translate("phrase.novc.description"))));
            return false;
        } else if (!isMemberInSameChannel()) {
            SafeMessage.sendMessage(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.error(invocation.translate("phrase.nosamevc.title"), invocation.translate("phrase.nosamevc.description"))));
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void saveQueue() {
        if (queueStorage.containsKey(guild.getIdLong()))
            queueStorage.replace(guild.getIdLong(), getQueue());
        else
            queueStorage.put(guild.getIdLong(), getQueue());
    }
}
