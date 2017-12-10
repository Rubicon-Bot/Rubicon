package fun.rubicon.listener;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
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
