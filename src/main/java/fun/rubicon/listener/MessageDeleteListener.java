package fun.rubicon.listener;

import fun.rubicon.commands.tools.CommandVote;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageDeleteListener extends ListenerAdapter{

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        CommandVote.handleMessageDeletion(event);
    }
}
