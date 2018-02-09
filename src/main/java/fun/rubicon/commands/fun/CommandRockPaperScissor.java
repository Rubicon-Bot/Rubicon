/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles the 'rockpaperscissor' command.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class CommandRockPaperScissor extends CommandHandler {

    public CommandRockPaperScissor() {
        super(new String[]{"rps", "rockpaperscissor"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.rps"), "Play rock paper scissor with the bot.", "<(r)ock/(p)aper/(s)cissor>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        return null;
    }
}
