/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.channel;

import fun.rubicon.core.entities.RubiconGuild;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class VoiceChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        if(RubiconGuild.fromGuild(event.getGuild()).getAutochannels().contains(event.getChannel().getIdLong())) {
            RubiconGuild.fromGuild(event.getGuild()).deleteAutochannel(event.getChannel().getIdLong());
        }
    }
}
