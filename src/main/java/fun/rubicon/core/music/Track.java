package fun.rubicon.core.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;

/**
 * Created by zekro on 22.11.2017 / 14:46
 * rubiconBot.fun.rubicon.core.music
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */


public class Track {

    private AudioTrack TRACK;
    private Member AUTHOR;

    public Track(AudioTrack track, Member author) {
        this.TRACK = track;
        this.AUTHOR = author;
    }

    public AudioTrack getTrack() {
        return TRACK;
    }

    public Member getAuthor() {
        return AUTHOR;
    }

}
