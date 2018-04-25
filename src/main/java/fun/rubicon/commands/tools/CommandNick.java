package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandNick extends CommandHandler {
    public CommandNick() {
        super(new String[] {"nick"}, CommandCategory.TOOLS, new PermissionRequirements("nick", false, false), "Easily nick you or others", "<nick/reset> [@User");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        return null;
    }
}
