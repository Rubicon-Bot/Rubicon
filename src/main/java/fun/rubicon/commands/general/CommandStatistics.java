package fun.rubicon.commands.general;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.DiscordCore;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */

public class CommandStatistics extends Command {

    public CommandStatistics(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        //Set EmbedBuilder
        EmbedBuilder builder = new EmbedBuilder();
        //Set EmbedBuilder Values
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor(Info.BOT_NAME + " Statistics", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.addField("Total servers", e.getJDA().getGuilds().size() + " Server", false);
        builder.addField("Total users", DiscordCore.getJDA().getUsers().stream().filter(u -> !u.isBot()).collect(Collectors.toList()).size() + " User", false);
        //Send Message with EmbedBuilder and delete it after a Minute
        e.getTextChannel().sendMessage(builder.build()).queue(message -> message.delete().queueAfter(60, TimeUnit.SECONDS));
    }

    @Override
    public String getDescription() {
        return "Sends bot stats.";
    }

    @Override
    public String getUsage() {
        return "statistics";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
