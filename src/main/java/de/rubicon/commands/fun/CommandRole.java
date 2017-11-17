package de.rubicon.commands.fun;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CommandRoll extends Command{
    public CommandRoll(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        int randomnumber, lownumber, highnumber;
        if(args.length<1)
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
            randomnumber = (int) (Math.random() * lownumber) + highnumber;
            sendEmbededMessage(e.getAuthor().getAsMention() + " rolls a " + randomnumber);
        }else {
            randomnumber = (int) (Math.random() * highnumber) + lownumber;
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