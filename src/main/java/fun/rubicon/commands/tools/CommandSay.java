package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.tools
 */

public class CommandSay extends Command {

    public CommandSay(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if(args.length < 2) {
            sendUsageMessage();
            return;
        }

        if(e.getMessage().getMentionedChannels().size() != 1) {
            sendUsageMessage();
            return;
        }

        String text = "";
        for(int i = e.getMessage().getMentionedChannels().get(0).getAsMention().split(" ").length; i < args.length; i++) {
            text += args[i] + " ";
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(e.getMember().getEffectiveName() + "'s Commands", null, e.getMember().getUser().getEffectiveAvatarUrl());
        builder.setDescription(text);
        builder.setColor(Colors.COLOR_PRIMARY);
        e.getMessage().getMentionedChannels().get(0).sendMessage(builder.build()).queue();
    }

    @Override
    public String getDescription() {
        return "Say something with the bot!";
    }

    @Override
    public String getUsage() {
        return "say <channel> <message>";
    }

    @Override
    public int getPermissionLevel() {
        return 1;
    }
}
