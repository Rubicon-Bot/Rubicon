package fun.rubicon.listener;

import fun.rubicon.commands.general.CommandGitBug;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class BugListener extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {
        CommandGitBug.handle(event);
    }

}
