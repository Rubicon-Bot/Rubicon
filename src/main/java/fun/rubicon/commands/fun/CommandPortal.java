package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandPortal extends Command {

    public CommandPortal(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

    }

    @Override
    public String getDescription() {
        return "Talk with users of other guilds.";
    }

    @Override
    public String getUsage() {
        return "portal open [guildid]" +
                "portal close";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
