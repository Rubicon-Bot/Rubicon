package fun.rubicon.listener;

import fun.rubicon.commands.tools.CommandVote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter{

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        CommandVote.reactVote(event);
    }
}
