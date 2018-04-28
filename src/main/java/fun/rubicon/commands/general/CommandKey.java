package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */

public class CommandKey extends CommandHandler {

    public CommandKey() {
        super(new String[]{"key", "redeem"}, CommandCategory.GENERAL, new PermissionRequirements("key", false, true), "Redeem an Gift Code or Generate one.", "<key>\ngen");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);

        switch (invocation.getArgs()[0]) {
            case "gen":
            case "generate":
                switch (invocation.getArgs()[1]) {
                    case "premium":
                        
                        break;
                    default:
                        return createHelpMessage(invocation);

                }

        }
        return null;
    }


}