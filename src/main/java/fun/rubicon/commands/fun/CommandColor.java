/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.util.Random;


public class CommandColor extends CommandHandler {
    public CommandColor() {
        super(new String[]{"color"}, CommandCategory.FUN, new PermissionRequirements(0, "command.color"), "Generates a random color.", "");
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //gen
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);

        Color randomColor = new Color(red, green, blue);
        String s = "!";
        System.out.println(s.toLowerCase());
        return new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Generated new color  Color:").setDescription("(R:" + randomColor.getRed() + " G:" + randomColor.getGreen() + " B:" + randomColor.getBlue() + ")").setColor(randomColor).build()).build();
    }
}
