/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.channel;

import fun.rubicon.core.entities.RubiconGuild;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class TextChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());

        if(rubiconGuild.hasJoinMessagesEnabled()) {
            if(event.getChannel().getIdLong() == rubiconGuild.getJoinMessage().getChannelId()) {
                rubiconGuild.deleteJoinMessage();
            }
        }
    }
}
