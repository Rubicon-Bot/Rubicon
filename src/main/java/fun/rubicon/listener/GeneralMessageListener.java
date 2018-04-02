package fun.rubicon.listener;

import fun.rubicon.commands.general.CommandYouTube;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class GeneralMessageListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {
        CommandYouTube.handle(event);
    }

}
