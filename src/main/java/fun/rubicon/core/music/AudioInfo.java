package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;

/**
 * Created by zekro on 22.11.2017 / 17:30
 * rubiconBot.fun.rubicon.core.music
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */


public class AudioInfo {

    private final AudioTrack track;
    private final Member author;

    AudioInfo(AudioTrack track, Member author) {
        this.track = track;
        this.author = author;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Member getAuthor() {
        return author;
    }

}