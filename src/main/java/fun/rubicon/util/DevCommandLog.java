package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class DevCommandLog {

    private static final long channelId = 407615131309178890L; //Dev

    public static void log(CommandManager.ParsedCommandInvocation invocation, long timeTook) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(
                "Date: " + new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date()) + "\n\n" +
                "Command: " + invocation.getCommandInvocation() + "\n" +
                "User: " + invocation.getAuthor().getName() + "(" + invocation.getAuthor().getId() + ")\n" +
                "Guild: " + invocation.getGuild().getName() + "(" + invocation.getGuild().getId() + ")\n" +
                "-------------------------\n" +
                "Execution Time: " + timeTook + "ms\n" +
                "Bot's ping: " + RubiconBot.getJDA().getPing() + "ms");
        RubiconBot.getJDA().getTextChannelById(channelId).sendMessage(builder.build()).queue();
    }
}
