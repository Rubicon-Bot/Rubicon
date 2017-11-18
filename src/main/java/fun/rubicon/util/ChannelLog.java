package fun.rubicon.util;

import fun.rubicon.core.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 18.11.2017 18:09
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class ChannelLog {
    public static void logCommand(String command, MessageReceivedEvent event){
        Guild guild = event.getGuild();
        MySQL SQL = Main.getMySQL();
        String prefix = SQL.getGuildValue(guild, "prefix");
        String logchannel = SQL.getGuildValue(guild, "logchannel");
        if (SQL.getGuildValue(guild, "logchannel").equals("0")) return;
        String us = event.getMember().getNickname();
        TextChannel channel = guild.getTextChannelById(logchannel);
        if(us == null) us = event.getAuthor().getName();
        event.getTextChannel().sendMessage(new EmbedBuilder().setDescription("[Command] `" + prefix +  command + "` was executed by **" + us + " (" + event.getAuthor().getName()+ "#" + event.getAuthor().getDiscriminator() + ")**").build()).queue();
    }

        }
