/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.util.stream.Collectors;

import static fun.rubicon.util.EmbedUtil.info;
import static fun.rubicon.util.EmbedUtil.message;

/**
 * Handles the 'statistics' command which responds with some statistics about this bot.
 *
 * @author ForYaSee, tr808axm
 */
public class CommandStatistics extends CommandHandler {
    /**
     * Constructs the 'statistics' command handler.
     */
    public CommandStatistics() {
        super(new String[]{"statistics", "statistic", "stats"}, CommandCategory.GENERAL,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.statistics"),
                "Shows some statistics about this bot.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = message(info(RubiconBot.getJDA().getSelfUser().getName() + "'s statistics", null)
                .addField("Total servers", String.valueOf(RubiconBot.getJDA().getGuilds().size()), true)
                .addField("Total users", String.valueOf(RubiconBot.getJDA().getUsers().stream()
                        .filter(u -> !u.isBot()).collect(Collectors.toList()).size()), true));
        textChannel.sendMessage(message).queue();
        return null;
    }
}
