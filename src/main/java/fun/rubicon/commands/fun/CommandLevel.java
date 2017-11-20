package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandLevel extends Command{


    public CommandLevel(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        MySQL LVL = Main.getMySQL();
        sendEmbededMessage("Your current Level: \n" + LVL.getUserValue(e.getAuthor(), "level") + "\n" + "Your current Points: \n" + LVL.getUserValue(e.getAuthor(), "points") + "\n Your Current Ruby´s: \n" + LVL.getUserValue(e.getAuthor(), "money"));
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
