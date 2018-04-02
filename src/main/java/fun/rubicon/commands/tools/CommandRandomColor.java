package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandRandomColor extends CommandHandler {

    public CommandRandomColor() {
        super(new String[]{"randomcolor","rcolor","randc"}, CommandCategory.TOOLS, new PermissionRequirements("", false, true), "", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        int r = ThreadLocalRandom.current().nextInt(255);
        int g = ThreadLocalRandom.current().nextInt(255);
        int b = ThreadLocalRandom.current().nextInt(255);

        Color color = new Color(r,g,b);

        String hex = "#"+Integer.toHexString(color.getRGB()).substring(2);

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(color)
                .setTitle("Random Color")
                .addField("Hexdezimalcode",hex,false)
                .addField("RGB Code",color.getRed()+","+color.getGreen()+","+color.getBlue(),false);

        return message(builder);
    }
}