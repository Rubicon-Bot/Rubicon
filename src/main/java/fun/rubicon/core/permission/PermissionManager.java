/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.permission;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.Main;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Can test a member's permissions to a command.
 *
 * @author ForYaSee, tr808axm
 * @deprecated Use {@link fun.rubicon.data.UserPermissions} instead.
 */
@Deprecated
public class PermissionManager {
    private Member member;
    private Command command;

    private UserPermissions userPermissions;
    private PermissionRequirements requirements;

    public PermissionManager(Member member, Command command) {
        this.member = member;
        this.command = command;

        // init new model
        userPermissions = new UserPermissions(member.getUser(), member.getGuild());
        requirements = new PermissionRequirements(command.getPermissionLevel(), command.getCommand());
    }

    public boolean hasPermission() {
        userPermissions.update();
        return requirements.coveredBy(userPermissions);
    }

    public String getPermissionsAsString() {
        return Main.getMySQL().getMemberValue(member, "permissions");
    }

    public int getPermissionLevel() {
        userPermissions.update();
        return userPermissions.getMemberPermissionLevel();
    }

    public String getAllAllowedCommands() {
        List<Command> allCommands = new ArrayList<>(CommandHandler.getCommands().values());
        StringBuilder permissionNodesString = new StringBuilder();
        for (Command cmd : allCommands)
            if (new PermissionManager(member, cmd).hasPermission())
                permissionNodesString.append(cmd.getCommand()).append(",");
        return permissionNodesString.toString();
    }

    public void addPermissions(String command) {
        String s = getPermissionsAsString();
        s += command.toLowerCase() + ",";
        Main.getMySQL().updateMemberValue(member, "permissions", s);
    }

    public void removePermission(String command) {
        String s = getPermissionsAsString();
        s = s.replace(command + ",", "");
        Main.getMySQL().updateMemberValue(member, "permissions", s);
    }

    public boolean containsPermission(String command) {
        return getPermissionsAsString().contains(command);
    }
}
