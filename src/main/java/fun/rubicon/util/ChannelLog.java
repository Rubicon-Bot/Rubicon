package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.command2.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package util
 */
public class ChannelLog {

    public static void logCommand(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        Guild guild = parsedCommandInvocation.invocationMessage.getGuild();
        MySQL SQL = RubiconBot.getMySQL();
        String prefix = SQL.getGuildValue(guild, "prefix");
        String logchannel = SQL.getGuildValue(guild, "logchannel");
        if (SQL.getGuildValue(guild, "logchannel").equals("0")) return;
        String us = parsedCommandInvocation.invocationMessage.getMember().getNickname();
        TextChannel channel = guild.getTextChannelById(logchannel);
        if (channel == null) return;
        if (us == null) us = parsedCommandInvocation.invocationMessage.getAuthor().getName();
        channel.sendMessage(new EmbedBuilder().setDescription("[Command] `" + prefix + parsedCommandInvocation.invocationCommand + "` was executed by **" + parsedCommandInvocation.invocationMessage.getAuthor().getName() + "#" + parsedCommandInvocation.invocationMessage.getAuthor().getDiscriminator() + " (" + parsedCommandInvocation.invocationMessage.getAuthor().getId() + ")**").build()).queue();
    }

}
