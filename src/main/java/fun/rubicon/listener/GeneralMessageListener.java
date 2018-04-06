package fun.rubicon.listener;

import fun.rubicon.commands.general.CommandYouTube;
import fun.rubicon.commands.music.QueueMessage;
import fun.rubicon.core.music.GuildMusicPlayer;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class GeneralMessageListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {
        new Thread(() -> CommandYouTube.handle(event)).start();
        new Thread(() -> { GuildMusicPlayer.handleTrackChoose(event); Thread.currentThread().setName("Track-chooser-"+ event.getMessage().getId() + "-Thread");}).start();
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        new Thread(() -> QueueMessage.handleMessageDeletion(event), "QueueMessageDeleteHandler-" + event.getMessageId()).start();
    }
}
