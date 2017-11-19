package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Foryase / Yannick
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
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
        for(int i = 1; i < args.length; i++) {
            text += args[i];
        }
        sendEmbededMessage(e.getMessage().getMentionedChannels().get(0), e.getMember().getEffectiveName(), Colors.COLOR_PRIMARY, text);
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
