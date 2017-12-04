package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */
public class CommandDonatemoney extends Command {
    public CommandDonatemoney(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

    }

    @Override
    public String getDescription() {
        return "You can give someone some Ruby's!";
    }

    @Override
    public String getUsage() {
        return "givemoney <UserAsMention> <amount>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
