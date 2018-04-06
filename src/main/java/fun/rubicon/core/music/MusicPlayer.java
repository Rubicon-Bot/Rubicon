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

import java.util.*;

/**
 * @author ForYaSee / Yannick Seeger
 */
public abstract class MusicPlayer extends AudioEventAdapterWrapped implements AudioSendHandler {

    protected final LavalinkManager lavalinkManager;
    private final Queue<AudioTrack> trackQueue;
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

    protected void initMusicPlayer(IPlayer player) {
        this.player = player;
        player.addListener(this);
    }

    public void play(AudioTrack track) {
        if (track == null) {
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

    public void queueTrack(AudioTrack audioTrack) {
        trackQueue.add(audioTrack);
        if(player.getPlayingTrack() == null)
            play(pollTrack());
    }

    public AudioTrack pollTrack() {
        return trackQueue.isEmpty() ? null : trackQueue.poll();
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
        if (repeating && !error) {
            queueTrack(track);
            return;
        }
        AudioTrack newTrack = pollTrack();
        queueTrack(newTrack);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.equals(AudioTrackEndReason.FINISHED)) {
            AudioTrack nextTrack = pollTrack();
            if(nextTrack == null) {
                closeAudioConnection();
                return;
            }
            play(nextTrack);
            return;
        }
        if (!endReason.equals(AudioTrackEndReason.LOAD_FAILED)) {
            Logger.debug(endReason.toString());
            handleTrackStop(player, track, false);
        } else {
            handleTrackStop(player, track, true);
        }
    }

    public abstract void closeAudioConnection();

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
