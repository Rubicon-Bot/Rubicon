package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;


/**
 * Rubicon Discord bot
 *
 * @author Lee Kappes / Lee
 * @copyright Rubicon Dev Team ${YEAR}
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.fun
 */

public class CommandColor extends Command{
    public CommandColor(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);

        Color randomColour = new Color(red,green,blue);
        String s= "!";
        System.out.println(s.toLowerCase());
        sendEmbededMessage(e.getTextChannel(), "Generated new color", randomColour, "Color: (R:" + randomColour.getRed()+ " G:"+randomColour.getGreen()+ " B:"+randomColour.getBlue()+")");
    }

    @Override
    public String getDescription() {
        return "Generates a random color.";
    }

    @Override
    public String getUsage() {
        return "color";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
