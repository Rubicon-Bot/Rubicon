package fun.rubicon.commands.guildowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.guildowner
 */
public class CommandBackup extends CommandHandler {

    public CommandBackup(String[] invocationAliases, CommandCategory category, PermissionRequirements permissionRequirements, String description, String usage) {
        super(invocationAliases, category, permissionRequirements, description, usage);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //TODO Work in Progress
        return null;
    }
}
