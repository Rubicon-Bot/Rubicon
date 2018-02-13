/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/**
 * Handles the 'randomcolor' command which generates random colors.
 *
 * @author LeeDJD, tr808axm
 */
public class CommandRandomColor extends CommandHandler {
    /**
     * Constructs the 'color' command handler.
     */
    public CommandRandomColor() {
        super(new String[]{"randomcolor", "rcolor", "randc"}, CommandCategory.TOOLS,
                new PermissionRequirements("command.color", false, true),
                "Generates a random color.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Color generatedColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        return message(success("Color generated", "Your new random color is RGB (`"
                + generatedColor.getRed() + "`, `" + generatedColor.getGreen() + "`, `" + generatedColor.getBlue() + "`).")
                .setColor(generatedColor));
    }
}