/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */
package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the 'clear' command.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class CommandClear extends CommandHandler {

    public CommandClear() {
        super(new String[]{"clear", "purge"}, CommandCategory.MODERATION, new PermissionRequirements("command.clear", false, false), "Clear the chat.", "<amount of messages> [@User]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 0) {
            return createHelpMessage();
        }

        if (!StringUtil.isNumeric(parsedCommandInvocation.getArgs()[0])) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Parameter must be numeric."));
        }

        int messageAmount = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
        User user = (parsedCommandInvocation.getMessage().getMentionedUsers().size() == 1) ? parsedCommandInvocation.getMessage().getMentionedUsers().get(0) : null;

        if (messageAmount < 2) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "I can't delete less than 2 messages."));
        }

        if (messageAmount > 3000) {
            return EmbedUtil.message(EmbedUtil.error("Error!", "Why do you want to clear more than 3000 messages??"));
        }

        int deletedMessagesSize = 0;
        List<Message> messagesToDelete;
        while (messageAmount != 0) {
            if (messageAmount > 100) {
                messagesToDelete = parsedCommandInvocation.getMessage().getTextChannel().getHistory().retrievePast(100).complete();
                messageAmount -= 100;
            } else {
                messagesToDelete = parsedCommandInvocation.getMessage().getTextChannel().getHistory().retrievePast(messageAmount).complete();
                messageAmount = 0;
            }
            messagesToDelete = messagesToDelete.stream().filter(message -> !message.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());
            if (user != null)
                messagesToDelete = messagesToDelete.stream().filter(message -> message.getAuthor() == user).collect(Collectors.toList());
            deletedMessagesSize += messagesToDelete.size();
            if (messagesToDelete.size() > 1)
                parsedCommandInvocation.getMessage().getTextChannel().deleteMessages(messagesToDelete).complete();
            else break;
        }
        return EmbedUtil.message(EmbedUtil.success("Cleared channel!", "Successfully cleared `" + deletedMessagesSize + "` messages"));
    }
}
