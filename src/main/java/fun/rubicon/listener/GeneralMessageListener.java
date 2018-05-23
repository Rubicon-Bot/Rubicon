package fun.rubicon.listener;

import fun.rubicon.commands.tools.CommandYouTube;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.core.music.QueueMessage;
import fun.rubicon.entities.Guild;
import fun.rubicon.entities.User;
import fun.rubicon.provider.GuildProvider;
import fun.rubicon.provider.UserProvider;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class GeneralMessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isFake() || event.getAuthor().isBot() || event.isWebhookMessage())
            return;
        User user = UserProvider.getUserById(event.getAuthor().getIdLong());
        Guild guild = GuildProvider.getGuildById(event.getGuild().getIdLong());
        guild.setPrefix("!!!");
        user.setBio(event.getMessage().getContentDisplay());
        guild.enableAutochannel(event.getGuild().getId(),"442396650825187329");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(guild.getAutochannels());

        new Thread(() -> CommandYouTube.handle(event), "YouTube-" + event.getMessage().getId() + "-Setup-Thread").start();
        new Thread(() -> GuildMusicPlayer.handleTrackChoose(event), "Track-chooser-" + event.getMessage().getId() + "-Thread").start();
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        new Thread(() -> QueueMessage.handleMessageDeletion(event));
    }
}
