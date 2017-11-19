package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Random;

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
        sendEmbededMessage(e.getTextChannel(), "Generated new Color", randomColour, "Color: (R:" + randomColour.getRed()+ " G:"+randomColour.getGreen()+ " B:"+randomColour.getBlue()+")");
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
