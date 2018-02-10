package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandFortnite extends CommandHandler {

    public CommandFortnite() {
        super(new String[]{"fortnite"}, CommandCategory.FUN, new PermissionRequirements(PermissionLevel.EVERYONE, "command.fortnite"), "Get your Fortnite statistics.", "");
    }

    /**
     * Method to be implemented by actual command handlers.
     *
     * @param parsedCommandInvocation the command arguments with prefix and command head removed.
     * @param userPermissions         an object to query the invoker's permissions.
     * @return a response that will be sent and deleted by the caller.
     */
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        return null;
    }
}
