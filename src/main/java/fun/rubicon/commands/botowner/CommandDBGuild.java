/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;
import static fun.rubicon.util.EmbedUtil.*;

public class CommandDBGuild extends CommandHandler {
    public CommandDBGuild() {
        super(new String[]{"dbguild"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements(PermissionLevel.BOT_AUTHOR, "command.dbguild"),
                "Manage database guild entries.", "<add | remove | default> <ServerID>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {

        if(parsedCommandInvocation.args.length <2){
            String option = parsedCommandInvocation.args[0];
            String serverID = parsedCommandInvocation.args[1];

            switch (option){
                case "default":
                    break;
                case "add":
                    break;
                case "remove":
                    RubiconBot.getMySQL().deleteGuild(serverID);
                    return message(error("Invalid parameter",option + " is not an valid parameter."));
                default:
                    return message(error("Invalid parameter",option + " is not an valid parameter."));
            }

            return null;
        }else{
            return message(error("Not enough arguments", "You forgot to add the option or server's ID."));
        }
    }

}
