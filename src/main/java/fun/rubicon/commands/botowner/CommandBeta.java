package fun.rubicon.commands.botowner;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;
import java.util.Map;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandBeta extends CommandHandler {

    public CommandBeta() {
        super(new String[]{"beta"}, CommandCategory.BOT_OWNER, new PermissionRequirements("beta", true, false), "Activate/Deactivate Beta Status", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
            int state = RubiconBot.getConfiguration().getInt("beta");
            switch (state) {
                case 1:
                    RubiconBot.getConfiguration().set("beta",0);
                    return message(success("Success", "Beta is now Deactivated!"));
                default:
                    RubiconBot.getConfiguration().set("beta",1);
                    return message(success("Success","Beta is now Activated!"));
            }

    }

}