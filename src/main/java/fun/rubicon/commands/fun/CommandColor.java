/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/**
 * Handles the 'color' command which generates random colors.
 * @author LeeDJD, tr808axm
 */
public class CommandColor extends CommandHandler {
    /**
     * Constructs the 'color' command handler.
     */
    public CommandColor() {
        super(new String[]{"color", "colour", "generate-color", "generate-colour", "random-color", "random-colour"}, CommandCategory.TOOLS,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.color"),
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
