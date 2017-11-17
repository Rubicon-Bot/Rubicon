package de.rubicon.commands.fun;

import de.rubicon.command.Command;
import de.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CommandRoll extends Command{
    public CommandRoll(String command, CommandCategory category, CommandCategory subcategory) {
        super(command, category, subcategory);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        int randomnumber, lownumber, highnumber;
        lownumber = Integer.valueOf(args[0]);
        highnumber = Integer.valueOf(args[1]);
        randomnumber = (int)(Math.random() *highnumber)+lownumber;
        e.getTextChannel().sendMessage(e.getAuthor().getAsMention()+ " rolls a " + randomnumber).queue(msg -> msg.delete().queueAfter(defaultDeleteSeconds, TimeUnit.SECONDS));
    }

    @Override
    public String getDescription() {
        return "Roll the dice.";
    }

    @Override
    public String getUsage() {
        return "roll lowNumber highNumber";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
