package fun.rubicon.commands.fun;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

<<<<<<< HEAD:src/main/java/de/rubicon/commands/fun/CommandRoll.java
<<<<<<< HEAD
import java.util.concurrent.TimeUnit;

public class CommandRoll extends Command {
=======
=======
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

>>>>>>> foryasee2:src/main/java/fun/rubicon/commands/fun/CommandRoll.java
public class CommandRoll extends Command{
>>>>>>> master
    public CommandRoll(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        int randomnumber, lownumber, highnumber;
        if(args.length<=1)
        {
            sendUsageMessage();
            return;
        }
        try {
            lownumber = Integer.parseInt(args[0]);
            highnumber = Integer.parseInt(args[1]);
        }catch (NumberFormatException exception){
            lownumber = highnumber = 0;
            sendErrorMessage("Only numbers allowed.");
            sendUsageMessage();
            return;
        }
        if(lownumber>highnumber) {
            randomnumber = ThreadLocalRandom.current().nextInt(highnumber, lownumber + 1);
            sendEmbededMessage(e.getAuthor().getAsMention() + " rolls a " + randomnumber);
        }else {
            randomnumber = ThreadLocalRandom.current().nextInt(lownumber, highnumber + 1);
            sendEmbededMessage(e.getAuthor().getAsMention() + " rolls a " + randomnumber);
        }
    }

    @Override
    public String getDescription() {
        return "Roll the dice.";
    }

    @Override
    public String getUsage() { return "roll <number> <number>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}