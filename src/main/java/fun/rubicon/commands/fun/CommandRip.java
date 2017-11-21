package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandRip extends Command{

    public CommandRip(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
