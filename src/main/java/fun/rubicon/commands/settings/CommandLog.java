package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandLog extends CommandHandler {
    public CommandLog() {
        super(new String[] {"log"}, CommandCategory.SETTINGS, new PermissionRequirements("logs", false, false), "Easy logging system WHOOO", "channel <#channel>\n member - Join/Leave log \nRole - Role assignments\n message - Message deletions");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return null;
    }
}
