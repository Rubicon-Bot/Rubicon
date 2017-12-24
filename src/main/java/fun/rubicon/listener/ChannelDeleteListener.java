/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener;

import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ChannelDeleteListener extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        //TODO Rework!
        /*if (e.getChannel().getName().equals("rubicon-portal")) {
            String stat = RubiconBot.getMySQL().getGuildValue(e.getGuild(), "portal");
            if (stat.contains("waiting")) {
                RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
                TextChannel textChannel;
                try {
                    textChannel = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                    textChannel.delete().queue();
                    sendPortalNotification(e.getGuild(), "Portal successfully closed!");
                } catch (Exception ignored) {

                }
            } else if (stat.contains("connected")) {
                RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "portal", "closed");
                TextChannel textChannel;
                try {
                    textChannel = e.getGuild().getTextChannelsByName("rubicon-portal", true).get(0);
                    textChannel.delete().queue();
                    sendPortalNotification(e.getGuild(), "Portal successfully closed!");
                } catch (Exception ignored) {

                }
                Guild otherGuild = e.getJDA().getGuildById(stat.split(":")[1]);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Portal Notification", null, e.getGuild().getIconUrl());
                builder.setDescription("Portal successfully closed!");
                builder.setColor(Colors.COLOR_PRIMARY);
                sendPortalNotification(otherGuild, "Portal was closed!");
                RubiconBot.getMySQL().updateGuildValue(otherGuild, "portal", "closed");
                try {
                    otherGuild.getTextChannelsByName("rubicon-portal", true).get(0).delete().queue();
                } catch (Exception ignored) {

                }
            }
        }*/
    }

    private void sendPortalNotification(Guild guild, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Portal Notification", null, guild.getIconUrl());
        builder.setDescription(message);
        builder.setColor(Colors.COLOR_PRIMARY);
        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.build()).queue());
    }
}