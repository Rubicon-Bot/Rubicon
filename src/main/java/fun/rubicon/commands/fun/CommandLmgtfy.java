package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.no_permissions;
import static fun.rubicon.util.EmbedUtil.success;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandLmgtfy extends CommandHandler {

    public CommandLmgtfy() {
        super(new String[]{"lmgtfy","letmegooglethatforyou"}, CommandCategory.FUN, new PermissionRequirements("lmgtfy", false, true), "Creates a let-me-google-that-for-you link.", "<search query>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length<1){
            return createHelpMessage();
        }
        return message(success("Created Link", "Send this link to the person who seems to need it:\n"
                + "https://lmgtfy.com/?iie=1&q=" + String.join("%20", invocation.getArgs()).replace("+", "%2B")));
    }

}