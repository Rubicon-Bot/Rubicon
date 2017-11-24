package fun.rubicon.commands.guildowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.listener
 */
public class CommandPrefix extends Command {

    public CommandPrefix(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length <= 1) {
            if(args.length == 0) {
                RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "prefix", "rc!");
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, e.getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `rc!`");
                e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
            } else {
                RubiconBot.getMySQL().updateGuildValue(e.getGuild(), "prefix", args[0]);
                EmbedBuilder builder = new EmbedBuilder();
                builder.setAuthor("Prefix updated", null, e.getGuild().getIconUrl());
                builder.setDescription(":white_check_mark: Successfully changed prefix to `" + args[0] + "`");
                e.getTextChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
            }
        } else {
            sendUsageMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Change the bot prefix";
    }

    @Override
    public String getUsage() {
        return "prefix [prefix]";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
