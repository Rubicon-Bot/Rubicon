/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command2;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Adapter to register commands handlers from the old API.
 *
 * @author tr808axm
 * @see CommandHandler
 * @see Command
 */
@SuppressWarnings("deprecation")
public class CommandHandlerAdapter extends CommandHandler {
    private final Command oldCommand;

    public CommandHandlerAdapter(Command oldCommand) {
        super(allInvocationAliases(oldCommand), oldCommand.getCategory(),
                new PermissionRequirements(oldCommand.getPermissionLevel(), oldCommand.getCommand()),
                oldCommand.getDescription(), oldCommand.getUsage());
        this.oldCommand = oldCommand;
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions permissions) {
        try {
            // re-create MessageReceivedEvent. response number should not be used in commands and can be set to a
            oldCommand.call(parsedCommandInvocation.args, new MessageReceivedEvent(RubiconBot.getJDA(),
                    -1, parsedCommandInvocation.invocationMessage));
        } catch (ParseException e) {
            throw new RuntimeException("Calling an old-API command caused a ParseException", e);
        }
        return null;
    }

    /**
     * Combine main alias (former 'command') and secondary aliases (former 'aliases') into the invocationAliases
     * String array.
     *
     * @param command the old command that the aliases are taken from.
     * @return invocationAliases string array.
     */
    private static String[] allInvocationAliases(Command command) {
        if (command.getAliases() == null) // no aliases
            return new String[]{command.getCommand()};
        else {
            String[] allInvocationAliases = new String[command.getAliases() == null ? 1 : (command.getAliases().size() + 1)];
            allInvocationAliases[0] = command.getCommand();
            for (int i = 0; i < command.getAliases().size(); i++)
                allInvocationAliases[i + 1] = command.getAliases().get(i);
            return allInvocationAliases;
        }
    }

    /**
     * @return the old-API command handler.
     */
    public Command getOldCommand() {
        return oldCommand;
    }
}
