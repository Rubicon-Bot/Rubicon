package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class DevCommandLog {

    private static final long channelId = 407615131309178890L; //Dev

    public static void log(CommandManager.ParsedCommandInvocation invocation) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(
                "User: " + invocation.getAuthor().getName() + "(" + invocation.getAuthor().getId() + ")\n" +
                        "Guild: " + invocation.getGuild().getName() + "(" + invocation.getGuild().getId() + ")\n" +
                        "-------------------------\n" +
                        "Command: " + invocation.getCommandInvocation() + "\n" +
                        "Bot's ping: " + RubiconBot.getJDA().getPing() + "ms");
        try {
            RubiconBot.getJDA().getTextChannelById(channelId).sendMessage(builder.build()).queue();
        } catch (Exception ignore) {

        }
    }
}
