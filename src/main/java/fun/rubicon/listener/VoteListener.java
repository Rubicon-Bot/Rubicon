package fun.rubicon.listener;

import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class VoteListener extends ListenerAdapter{

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        CommandPoll.reactVote(event);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        CommandPoll.handleMessageDeletion(event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        CommandPoll.handleReactionRemove(event);
    }
}
