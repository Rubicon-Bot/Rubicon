package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zekro on 22.11.2017 / 14:44
 * rubiconBot.fun.rubicon.core.music
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */


public class Manager extends AudioEventAdapter {

    private AudioPlayer PLAYER;
    private Queue<Track> queue;


    public Manager(AudioPlayer player) {
        this.PLAYER = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Queue an audio track.
     * @param track Track to Queue
     * @param author Member who queued the track
     */
    public void queue(AudioTrack track, Member author) {

        Track audioTrack = new Track(track, author);
        queue.add(audioTrack);

        if (PLAYER.getPlayingTrack() == null)
            PLAYER.playTrack(track);

    }

    public Set<Track> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    public Track getQueuedTrack(AudioTrack track) {
        return queue.stream().filter(t -> t.getTrack().equals(track)).findFirst().orElse(null);
    }

    public void purge() {
        queue.clear();
    }

    public void shuffle() {
        List<Track> cQueue = new ArrayList<>(getQueue());
        Track cTrack = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, cTrack);
        purge();
        queue.addAll(cQueue);
    }


    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

        Track audioTrack = queue.element();
        VoiceChannel vchan = audioTrack.getAuthor().getVoiceState().getChannel();

        if (vchan == null)
            player.stopTrack();
        else
            audioTrack.getAuthor().getGuild().getAudioManager().openAudioConnection(vchan);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        Guild g = queue.poll().getAuthor().getGuild();

        if (queue.isEmpty())
            g.getAudioManager().closeAudioConnection();
        else
            player.playTrack(queue.element().getTrack());
    }

}
