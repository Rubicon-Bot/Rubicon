/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Bitly;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'shorten' command.
 * Can shorten URLs.
 *
 * @author DerSchlaubi, tr808axm
 */
public class CommandShorten extends CommandHandler {
    private final Bitly bitlyAPI;

    /**
     * Constructs the CommandHandler.
     */
    public CommandShorten() {
        super(new String[]{"shorten", "short", "bitly", "schlb.pw"}, CommandCategory.TOOLS,
                new PermissionRequirements(0, "command.shorten"),
                "Shortens a URL with schlb.pw", "<URL>");
        bitlyAPI = new Bitly(Info.BITLY_TOKEN);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.args.length == 0) {
            // no URL specified
            return createHelpMessage(invocation);
        } else if (invocation.args.length == 1) {
            String shortURL;
            try {
                shortURL = bitlyAPI.generateShortLink(invocation.args[0]);
            } catch (Exception e) {
                // unknown exception in request through HttpRequest
                Logger.error(e);
                return message(error("Unknown error", "An unknown error occurred while fetching your short url."));
            }
            return message(shortURL == null
                    // invalid URL
                    ? error("Invalid URL", "'" + invocation.args[0] + "' is not a valid URL.")
                    // shortening successful
                    : success("URL shortened", "Your URL was shortened:\n" + shortURL));
        } else {
            // more than 1 arguments -> URL contains whitespaces
            return message(error("Invalid URL", "You can not use whitespaces in a URL."));
        }
    }
}
