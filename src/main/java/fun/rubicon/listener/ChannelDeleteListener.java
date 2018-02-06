/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        String portalStatus = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "portal");
        if (portalStatus.equalsIgnoreCase("closed") || portalStatus.equalsIgnoreCase("waiting"))
            return;
        if (portalStatus.equalsIgnoreCase("open")) {
            TextChannel channel = e.getJDA().getTextChannelById(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "channelid"));
            if (e.getChannel().getId() != channel.getId())
                return;

            Guild partnerGuild = e.getJDA().getGuildById(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "partnerid"));
            RubiconBot.getMySQL().deletePortal(e.getGuild());
            RubiconBot.getMySQL().deletePortal(partnerGuild);
            RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
            RubiconBot.getMySQL().updateGuildValue(partnerGuild, "portal", "closed");

            sendPortalNotification(e.getGuild(), "Portal was closed.");
            sendPortalNotification(partnerGuild, "Portal was closed on the other side.");
        }
    }

    private void sendPortalNotification(Guild guild, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Portal Notification", null, guild.getIconUrl());
        builder.setDescription(message);
        builder.setColor(Colors.COLOR_PRIMARY);
        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.build()).queue());
    }
}