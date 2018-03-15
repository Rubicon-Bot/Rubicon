package fun.rubicon.commands.admin;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPortal extends CommandHandler {

    public CommandPortal() {
        super(new String[]{"portal"}, CommandCategory.ADMIN, new PermissionRequirements("portal", false, false), "Create a portal and talk with users of other servers.",
                "create [serverId] | Opens a new portal\n" +
                        "close | Closes your portal\n" +
                        "info | Shows info about the current portal\n" +
                        "settings | Customize the portal mechanic\n" +
                        "kick <serverId> | Votekicks a server out of the portal.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return null;
    }
}
