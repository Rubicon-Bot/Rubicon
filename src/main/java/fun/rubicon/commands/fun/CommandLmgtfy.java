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
import fun.rubicon.util.Bitly;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/**
 * Handles the 'lmgtfy' command which creates google search links using lmgtfy.com.
 * @author LeeDJD, tr808axm
 */
public class CommandLmgtfy extends CommandHandler {
    private final Bitly bitlyAPI;

    /**
     * Constructs this command handler.
     */
    public CommandLmgtfy() {
        super(new String[]{"lmgtfy", "letmegooglethatforyou", "let-me-google-that-for-you"}, CommandCategory.TOOLS,
                new PermissionRequirements(0, "command.lmgtfy"),
                "Creates a let-me-google-that-for-you link.", "<search query...>");
        bitlyAPI = new Bitly(Info.BITLY_TOKEN);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (invocation.args.length == 0)
            return createHelpMessage(invocation);
        else
            return message(success("Created lmgtfy link",
                    "Send this link to the person who seems to need it:\n"
                            + "https://lmgtfy.com/?iie=1&q=" + String.join("%20", invocation.args).replace("+", "%2B")));
    }
}