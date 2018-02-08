/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandAutochannel;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.commands.botowner.CommandBroadcast;
import fun.rubicon.commands.tools.CommandVote;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().equals(RubiconBot.getJDA().getSelfUser()))
            return;
        CommandVote.reactVote(event);
        CommandBroadcast.handleReaction(event);
        CommandAutochannel.handleReaction(event);
        CommandVerification.handleReaction(event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

    }
}
