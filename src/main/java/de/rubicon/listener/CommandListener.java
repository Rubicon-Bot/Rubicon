package de.rubicon.listener;

import de.rubicon.command.CommandHandler;
import de.rubicon.core.Main;
import de.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e) {
        try {
            if (e.getAuthor() != e.getJDA().getSelfUser() && !e.getAuthor().isBot()) {
                if (e.getMessage().getContent().toLowerCase().startsWith(Info.BOT_DEFAULT_PREFIX.toLowerCase())) {
                    CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent(), e));
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
