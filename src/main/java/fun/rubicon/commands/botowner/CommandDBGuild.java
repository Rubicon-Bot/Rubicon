/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.*;

public class CommandDBGuild extends CommandHandler {
    public CommandDBGuild() {
        super(new String[]{"dbguild", "dbguilds"}, CommandCategory.BOT_OWNER,
                new PermissionRequirements("command.dbguild", true, false),
                "Manage database guild entries.", "<add | remove | default> <ServerID>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.getArgs().length == 2) {
            String option = parsedCommandInvocation.getArgs()[0];
            String serverID = parsedCommandInvocation.getArgs()[1];

            switch (option) {
                case "default":
                    RubiconBot.getMySQL().deleteGuild(serverID);
                    RubiconBot.getMySQL().createGuildServer(serverID);
                    return message(success("Server set to default", "The Server with the ID " + serverID + " has been set to default."));
                case "add":
                    RubiconBot.getMySQL().createGuildServer(serverID);
                    return message(success("Server added", "The Server with the ID " + serverID + " has been added successfully."));
                case "remove":
                    RubiconBot.getMySQL().deleteGuild(serverID);
                    return message(success("Server removed", "The Server with the ID " + serverID + " has been removed successfully."));
                default:
                    return message(error("Invalid parameter", option + " is not an valid parameter."));
            }
        } else {
            return message(error("Not enough arguments", "You forgot to add the option or server's ID."));
        }
    }

}
