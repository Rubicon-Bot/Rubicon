package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;

import java.util.*;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class MusicPlayer extends AudioEventAdapterWrapped implements AudioSendHandler {

    private static List<Long> guildInstances = new ArrayList<>();

    protected final LavalinkManager lavalinkManager;
    private Queue<AudioTrack> trackQueue;
    private IPlayer player;
    private boolean repeating;
    private boolean stayInChannel;

    protected final int DEFAULT_VOLUME = 10;
    protected final int QUEUE_MAXIMUM = 50;

    public MusicPlayer() {
        lavalinkManager = RubiconBot.getLavalinkManager();
        trackQueue = new LinkedList<>();
        repeating = false;
        //When this variable is true you will die automatically
        stayInChannel = false;
    }

    protected void initMusicPlayer(Guild guild, IPlayer player) {
        this.player = player;
        //if(!guildInstances.contains(guild.getIdLong())) {
        //    guildInstances.add(guild.getIdLong());
        //    player.addListener(this);
        //}
    }

    public void play(AudioTrack track) {
        if (track == null) {
            closeAudioConnection();
            return;
        }
        if (player.isPaused())
            player.setPaused(false);
        player.playTrack(track);
    }

    public void stop() {
        player.stopTrack();
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);
    }

    public void seek(long time) {
        player.seekTo(time);
    }

    public void shuffle() {
        Collections.shuffle((List<?>) trackQueue);
    }

    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    public int getVolume() {
        return player.getVolume();
    }

    public void setRepeating(boolean repeating) {
        repeating = repeating;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setStayingInChannel(boolean stayInChannel) {
        this.stayInChannel = stayInChannel;
    }

    public boolean isStayingInChannel() {
        return stayInChannel;
    }

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public long getTrackPosition() {
        return player.getTrackPosition();
    }

    public void setQueue(Queue<AudioTrack> queue) {
        this.trackQueue = queue;
    }

    public Queue<AudioTrack> getQueue() {
        return trackQueue;
    }

    public void queueTrack(AudioTrack audioTrack) {
        trackQueue.add(audioTrack);
        saveQueue();
        Logger.debug(getQueueSize() + "");
        if (player.getPlayingTrack() == null)
            play(pollTrack());
    }

    public AudioTrack pollTrack() {
        if (trackQueue.isEmpty())
            return null;
        AudioTrack track = trackQueue.poll();
        saveQueue();
        return track;
    }

    public void clearQueue() {
        trackQueue.clear();
    }

    public List<AudioTrack> getTrackList() {
        return new ArrayList<>(trackQueue);
    }

    public int getQueueSize() {
        return trackQueue.size();
    }

    private void handleTrackStop(AudioPlayer player, AudioTrack track, boolean error) {
        AudioTrack newTrack;
        if (repeating && !error)
            newTrack = track;
        else
            newTrack = pollTrack();
        if (newTrack != null) {
            queueTrack(newTrack);
        } else
            closeAudioConnection();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        handleTrackStop(player, track, endReason.equals(AudioTrackEndReason.LOAD_FAILED));
    }

    protected abstract void closeAudioConnection();

    protected abstract void saveQueue();

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        handleTrackStop(player, track, true);
    }

    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public byte[] provide20MsAudio() {
        return new byte[0];
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}
