/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.data;

/**
 * Permission requirements.
 *
 * @author tr808axm
 */
public class PermissionRequirements {
    private enum PermissionLevel {
        EVERYONE(0),
        WITH_PERMISSION(1),
        ADMINISTRATOR(2),
        SERVER_OWNER(3),
        BOT_AUTHOR(4);

        public final int value;

        PermissionLevel(int value) {
            this.value = value;
        }

        public static PermissionLevel getByValue(int value) {
            for (PermissionLevel level : values())
                if (level.value == value)
                    return level;
            return null;
        }
    }

    private final PermissionLevel requiredPermissionLevel;
    private final String requiredPermissionNode;

    public PermissionRequirements(int requiredPermissionLevel, String requiredPermissionNode) {
        this.requiredPermissionLevel = PermissionLevel.getByValue(requiredPermissionLevel);
        this.requiredPermissionNode = requiredPermissionNode;
    }

    public boolean coveredBy(UserPermissions userPermissions) {
        if (userPermissions.isBotAuthor())
            return true;
        else if (requiredPermissionLevel == PermissionLevel.BOT_AUTHOR)
            return false;

        if (userPermissions.getMemberPermissionLevel() > requiredPermissionLevel.value)
            return true;

        if (requiredPermissionLevel == PermissionLevel.WITH_PERMISSION && userPermissions.hasPermissionNode(requiredPermissionNode))
            return true;

        if (requiredPermissionLevel == PermissionLevel.EVERYONE)
            return true;

        if (requiredPermissionLevel == PermissionLevel.ADMINISTRATOR && userPermissions.isAdministrator())
            return true;

        return requiredPermissionLevel == PermissionLevel.SERVER_OWNER && userPermissions.isServerOwner();

    }
}
