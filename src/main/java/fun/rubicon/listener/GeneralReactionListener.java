/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.commands.settings.CommandAutochannel;
import fun.rubicon.core.entities.RubiconGiveaway;
import fun.rubicon.core.music.GuildMusicPlayer;
import fun.rubicon.core.music.QueueMessage;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class GeneralReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        // Start in new Thread to prevent blocking
        new Thread(() -> CommandAutochannel.handleReaction(event)).start();
        new Thread(() -> QueueMessage.handleReaction(event)).start();
        new Thread(() -> RubiconGiveaway.handleReaction(event)).start();
    }
}
