package fun.rubicon.listener;

import fun.rubicon.commands.general.CommandYouTube;
import fun.rubicon.core.music.GuildMusicPlayer;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class GeneralMessageListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {
        new Thread(() -> CommandYouTube.handle(event),"YouTube-"+event.getMessage().getId()+"-Setup-Thread").start();
        new Thread(() ->  GuildMusicPlayer.handleTrackChoose(event), "Track-chooser-"+ event.getMessage().getId() + "-Thread").start();
    }

}
