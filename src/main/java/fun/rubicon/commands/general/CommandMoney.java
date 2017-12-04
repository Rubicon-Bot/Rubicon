/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import com.sun.java.util.jar.pack.Instruction;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

public class CommandMoney extends CommandHandler {
    public CommandMoney() {
        super(new String[]{"money","ruby"}, CommandCategory.GENERAL,
                new PermissionRequirements(0, "command.money"),
                "You can donate Ruby's to someone!", "money <give | set | add | remove> <UserAsMention> <amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        switch (args[0]){
            case "give":
                break;
            case "set":
                break;
            case "add":
                break;
            case "remove":
                break;
        }
    }
}
