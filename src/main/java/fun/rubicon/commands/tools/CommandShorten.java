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
import fun.rubicon.util.Bitly;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Message;

import javax.xml.ws.http.HTTPException;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'shorten' command.
 * Can shorten URLs.
 *
 * @author DerSchlaubi, tr808axm
 */
public class CommandShorten extends CommandHandler {
    /**
     * Constructs the CommandHandler.
     */
    public CommandShorten() {
        super(new String[]{"shorten", "short", "bitly", "schlb.pw"}, CommandCategory.TOOLS,
                new PermissionRequirements("command.shorten", false, true),
                "Shortens a URL with schlb.pw", "<URL>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            // no URL specified
            return createHelpMessage(invocation);
        } else if (invocation.getArgs().length == 1) {
            String shortURL;
            try {
                shortURL = Bitly.shorten(invocation.getArgs()[0]);
            } catch (IllegalArgumentException e) {
                return message(error("Invalid URL", "`" + invocation.getArgs()[0] + "` is not a valid URL."));
            } catch (HTTPException e) {
                Logger.error("Invalid bit.ly response");
                Logger.error(e);
                return message(error("Service problem", "The URL shortening service responded with an error."));
            } catch (RuntimeException e) {
                // invalid response code
                Logger.error(e);
                return message(error("Unknown error", "An unknown error occurred while fetching your short url."));
            }
            return message(success("URL shortened", "Your URL was shortened: " + shortURL));
        } else {
            // more than 1 arguments -> URL contains whitespaces
            return message(error("Invalid URL", "You can not use whitespaces in a URL."));
        }
    }
}
