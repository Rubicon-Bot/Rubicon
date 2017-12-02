package fun.rubicon.listener;

import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandGiveaway;
import fun.rubicon.commands.botowner.CommandBroadcast;
import fun.rubicon.commands.tools.CommandVote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter{

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot())
            return;
        CommandVote.reactVote(event);
        CommandGiveaway.handleReaction(event);
        CommandBroadcast.handleReaction(event);
        CommandAutochannel.handleReaction(event);
    }
}
