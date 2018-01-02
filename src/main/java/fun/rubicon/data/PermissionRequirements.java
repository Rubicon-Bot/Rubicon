/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.data;

import fun.rubicon.permission.Permission;

/**
 * Permission requirements.
 *
 * @author tr808axm
 */
public class PermissionRequirements {
    private final PermissionLevel requiredPermissionLevel;
    private final String requiredPermissionNode;

    /**
     * Constructs a new PermissionRequirements object.
     *
     * @param requiredPermissionLevel the required permission level. See {@link PermissionLevel} for value definitions.
     * @param requiredPermissionNode  the permission node that allows a user to pass the permission check.
     * @deprecated Use the constructor with {@link PermissionLevel} instead.
     */
    @Deprecated
    public PermissionRequirements(int requiredPermissionLevel, String requiredPermissionNode) {
        this(PermissionLevel.getByValue(requiredPermissionLevel), requiredPermissionNode);
    }

    /**
     * Constructs a new PermissionRequirements object.
     *
     * @param requiredPermissionLevel defines which group of users pass these requirements.
     * @param requiredPermissionNode  the permission node that allows a user to pass the permission check.
     */
    public PermissionRequirements(PermissionLevel requiredPermissionLevel, String requiredPermissionNode) {
        this.requiredPermissionLevel = requiredPermissionLevel;
        this.requiredPermissionNode = requiredPermissionNode;
    }

    /**
     * Checks whether the conditions set in this object are met by a user's permissions.
     * @param userPermissions the user permissions access object.
     * @return true if all conditions are met, false otherwise.
     */
    public boolean coveredBy(UserPermissions userPermissions) {
        // bot author
        if (userPermissions.isBotAuthor())
            return true;
        else if (requiredPermissionLevel == PermissionLevel.BOT_AUTHOR)
            return false;

        // server owner
        if(userPermissions.isServerOwner())
            return true;

        Permission effectiveEntry = userPermissions.getEffectivePermissionEntry(null, requiredPermissionNode);
        if(effectiveEntry == null) {
            // defaults
            return requiredPermissionLevel == PermissionLevel.EVERYONE; // everyone -> true, with_permission -> false
        } else
            // check permission
            return !effectiveEntry.isNegated();
    }

    /**
     * @return the PermissionLevel requirement.
     */
    public PermissionLevel getRequiredPermissionLevel() {
        return requiredPermissionLevel;
    }

    /**
     * @return the permission node that lets a user pass
     */
    public String getRequiredPermissionNode() {
        return requiredPermissionNode;
    }
}
