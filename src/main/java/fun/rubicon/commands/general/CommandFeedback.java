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

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'feedback' command which sends a feedback message to the developer server.
 */
public class CommandFeedback extends CommandHandler {
    /**
     * Constructs this CommandHandler.
     */
    public CommandFeedback() {
        super(new String[]{"feedback", "submitidea", "submit-idea"}, CommandCategory.GENERAL,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.feedback"),
                "Sends a feedback message to the developers.", "<message (min. 3 words)>");
    }
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (invocation.args.length < 3)
            return message(error("Text too short", "Please use at least three words in your feedback message."));
        else {
            RubiconBot.getJDA().getTextChannelById(383324255380701194L).sendMessage(
                    message(embed("New feedback", "```" + invocation.invocationMessage.getContentDisplay().replace(invocation.serverPrefix + invocation.invocationCommand + " ", ""))
                            .setAuthor(invocation.invocationMessage.getAuthor().getName() + '#'
                                    + invocation.invocationMessage.getAuthor().getDiscriminator(), null,
                                    invocation.invocationMessage.getAuthor().getEffectiveAvatarUrl()))).queue();
            return message(success("Feedback sent", "Your feedback was submitted to the developers. Thank you!"));
        }
    }
}
