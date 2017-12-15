package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
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