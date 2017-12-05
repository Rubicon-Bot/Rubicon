package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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

public class CommandColor extends CommandHandler{
    public CommandColor() {
        super(new String[]{"color"},CommandCategory.FUN,new PermissionRequirements(0,"command.color"),"Generates a random color.","");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //gen
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);

        Color randomColour = new Color(red,green,blue);
        String s= "!";
        System.out.println(s.toLowerCase());
        return new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Generated new color  Color:").setDescription("(R:" + randomColour.getRed()+ " G:"+randomColour.getGreen()+ " B:"+randomColour.getBlue()+")").build()).build();
    }
}
