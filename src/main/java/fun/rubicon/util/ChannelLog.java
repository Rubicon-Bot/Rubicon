package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package util
 */
public class ChannelLog {
    public static void logCommand(String command, MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        MySQL SQL = RubiconBot.getMySQL();
        String prefix = SQL.getGuildValue(guild, "prefix");
        String logchannel = SQL.getGuildValue(guild, "logchannel");
        if (SQL.getGuildValue(guild, "logchannel").equals("0")) return;
        String us = event.getMember().getNickname();
        TextChannel channel = guild.getTextChannelById(logchannel);
        if (channel == null) return;
        if (us == null) us = event.getAuthor().getName();
        channel.sendMessage(new EmbedBuilder().setDescription("[Command] `" + prefix + command + "` was executed by **" + us + " (" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ")**").build()).queue();
    }

}
