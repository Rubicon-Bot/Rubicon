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

import java.util.concurrent.ThreadLocalRandom;

public class CommandRoll extends Command{
    public CommandRoll(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        int randomNumber, lowNumber, highNumber;
        if(args.length <= 1) {
            sendUsageMessage();
            return;
        }
        try {
            lowNumber = Integer.parseInt(args[0]);
            highNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sendErrorMessage("Only numbers allowed.");
            sendUsageMessage();
            return;
        }
        if(lowNumber > highNumber) {
            randomNumber = ThreadLocalRandom.current().nextInt(highNumber, lowNumber + 1);
            sendEmbededMessage(e.getAuthor().getAsMention() + " rolls a " + randomNumber);
        } else {
            randomNumber = ThreadLocalRandom.current().nextInt(lowNumber, highNumber + 1);
            sendEmbededMessage(e.getAuthor().getAsMention() + " rolls a " + randomNumber);
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
        return 0; //TODO? or JavaDocs
    }
}
