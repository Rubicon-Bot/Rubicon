/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.channel;

import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.features.portal.Portal;
import fun.rubicon.features.portal.PortalManager;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

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
        if(rubiconGuild.hasLeaveMessagesEnabled()) {
            if(event.getChannel().getIdLong() == rubiconGuild.getLeaveMessage().getChannelId()) {
                rubiconGuild.deleteLeaveMessage();
            }
        }

        if(rubiconGuild.hasPortal()) {
            PortalManager portalManager = new PortalManager();
            Portal portal = portalManager.getPortalByOwner(rubiconGuild.getPortalRoot());
            if(portal.containsChannel(event.getChannel())) {
                portal.removeGuild(event.getGuild().getId());
                rubiconGuild.closePortal();
                //TODO TEST THIS
            }
        }
    }
}
