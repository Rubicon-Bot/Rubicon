/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PortalListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (!e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
            if (e.getChannel().getId().equals(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "channelid"))) {
                String status = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "portal");
                if (status.contains("open")) {
                    TextChannel otherChannel = e.getJDA().getTextChannelById(RubiconBot.getMySQL().getPortalValue(e.getJDA().getGuildById(RubiconBot.getMySQL().getPortalValue(e.getGuild(), "partnerid")), "channelid"));
                    try {
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setAuthor(e.getAuthor().getName(), null, e.getAuthor().getEffectiveAvatarUrl());
                        builder.setDescription(e.getMessage().getContent());
                        otherChannel.sendMessage(builder.build()).queue();
                    } catch (NullPointerException fuck) {
                        fuck.printStackTrace();
                    }
                }
            }
        }
    }
}
