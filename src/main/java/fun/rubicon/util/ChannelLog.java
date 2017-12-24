/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.command2.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

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
        channel.sendMessage(new EmbedBuilder().setDescription("[Command] `" + prefix + parsedCommandInvocation.invocationCommand + "` was executed by **" + us + " (" + parsedCommandInvocation.invocationMessage.getAuthor().getName() + "#" + parsedCommandInvocation.invocationMessage.getAuthor().getDiscriminator() + ")**").build()).queue();
    }

}
