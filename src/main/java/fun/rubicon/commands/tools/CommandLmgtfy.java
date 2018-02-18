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

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/**
 * Handles the 'lmgtfy' command which creates google search links using lmgtfy.com.
 *
 * @author LeeDJD, tr808axm
 */
public class CommandLmgtfy extends CommandHandler {
    /**
     * Constructs this command handler.
     */
    public CommandLmgtfy() {
        super(new String[]{"lmgtfy", "letmegooglethatforyou", "let-me-google-that-for-you"}, CommandCategory.TOOLS,
                new PermissionRequirements("command.lmgtfy", false, true),
                "Creates a let-me-google-that-for-you link.", "<search query...>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        else
            return message(success("Created lmgtfy link",
                    "Send this link to the person who seems to need it:\n"
                            + "https://lmgtfy.com/?iie=1&q=" + String.join("%20", invocation.getArgs()).replace("+", "%2B")));
    }
}