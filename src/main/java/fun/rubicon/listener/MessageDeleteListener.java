/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.commands.tools.CommandVote;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageDeleteListener extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        CommandVote.handleMessageDeletion(event);
    }
}
