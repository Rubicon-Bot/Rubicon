/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.command;

import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * Handles a command handler that is currently not working.
 *
 * @author tr808axm
 */
public class UnavailableCommandHandler extends CommandHandler {
    /**
     * Constructs an unavailable command handler out of original data.
     *
     * @param invocationAliases      the invocation commands (aliases). First entry is the 'main' alias.
     * @param category               the {@link CommandCategory} this command belongs to.
     * @param permissionRequirements all permission requirements a user needs to meet to execute a command.
     * @param description            a short command description.
     * @param parameterUsage         the usage message.
     */
    public UnavailableCommandHandler(String[] invocationAliases, CommandCategory category, PermissionRequirements permissionRequirements, String description, String parameterUsage) {
        super(invocationAliases, category, permissionRequirements, description, parameterUsage);
    }

    /**
     * Constructs an unavailable command handler an unavailable command handler's data.
     *
     * @param unavailableCommandHandler the CommandHandler whose information will be used.
     */
    public UnavailableCommandHandler(CommandHandler unavailableCommandHandler) {
        super(unavailableCommandHandler.getInvocationAliases(), unavailableCommandHandler.getCategory(),
                unavailableCommandHandler.getPermissionRequirements(), unavailableCommandHandler.getDescription(),
                unavailableCommandHandler.getParameterUsage());
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        return EmbedUtil.message(EmbedUtil.error("Feature not available!",
                "Sorry, this feature is not available right now."));
    }
}
