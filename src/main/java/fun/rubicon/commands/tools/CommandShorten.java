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
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

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
                "Shortens a URL with schlb.pw", "shorten <URL>");
        bitlyAPI = new Bitly(Info.BITLY_TOKEN);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length == 0) {
            // no URL specified
            return new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_SECONDARY)
                    .setTitle(":information_source: '" + parsedCommandInvocation.invocationCommand + "' command help")
                    .setDescription(getDescription())
                    .addField("Aliases", String.join(", ", getInvocationAliases()), false)
                    .addField("Usage", getUsage(), false)
                    .build()).build();
        } else if (parsedCommandInvocation.args.length == 1) {
            String shortURL;
            try {
                shortURL = bitlyAPI.generateShortLink(parsedCommandInvocation.args[0]);
            } catch (Exception e) {
                // unknown exception in request through HttpRequest
                Logger.error(e);
                return new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setColor(Colors.COLOR_ERROR)
                        .setTitle(":warning: Unknown error")
                        .setDescription("An unknown error occurred while fetching your short url.")
                        .build()).build();
            }
            return shortURL == null
                    // invalid URL
                    ? new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_ERROR)
                    .setTitle(":warning: Invalid URL")
                    .setDescription("'" + parsedCommandInvocation.args[0] + "' is not a valid URL.")
                    .build()).build()
                    // shortening successful
                    : new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_PRIMARY)
                    .setTitle(":white_check_mark: URL shortened")
                    .setDescription("Your URL was shortened:\n" + shortURL)
                    .build()).build();
        } else {
            // more than 1 arguments -> URL contains whitespaces
            return new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setColor(Colors.COLOR_ERROR)
                    .setTitle(":warning: Invalid URL")
                    .setDescription("You can not use whitespaces in a URL.")
                    .build()).build();
        }
    }
}
