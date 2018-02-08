/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

/**
 * Permission requirements.
 *
 * @author tr808axm
 */
public class PermissionRequirements {
    private final String requiredPermissionNode;
    private final boolean isAuthorExclusive;
    private final boolean isDefault;

    /**
     * Constructs a new PermissionRequirements object.
     *
     * @param requiredPermissionNode the permission node that allows a user to pass the permission check.
     * @param isAuthorExclusive      whether this permission is exclusively granted for bot authors.
     * @param isDefault              whether users should have this permission by default (if no other permission entry covers it).
     */
    public PermissionRequirements(String requiredPermissionNode, boolean isAuthorExclusive, boolean isDefault) {
        this.requiredPermissionNode = requiredPermissionNode;
        this.isAuthorExclusive = isAuthorExclusive;
        this.isDefault = isDefault;
    }

    /**
     * Checks whether the conditions set in this object are met by a user's permissions.
     *
     * @param userPermissions the user permissions access object.
     * @return true if all conditions are met, false otherwise.
     */
    public boolean coveredBy(UserPermissions userPermissions) {
        // bot authors have all permissions
        if (userPermissions.isBotAuthor())
            return true;

        // author exclusive permissions are not accessible for other users
        if (isAuthorExclusive)
            return false;

        // server owner has all perms on his server
        if (userPermissions.isServerOwner())
            return true;

        Permission effectiveEntry = userPermissions.getEffectivePermissionEntry(null, requiredPermissionNode);
        if (effectiveEntry == null) {
            // defaults
            return isDefault;
        } else
            // check permission
            return !effectiveEntry.isNegated();
    }

    /**
     * @return the permission node that lets a user pass
     */
    public String getRequiredPermissionNode() {
        return requiredPermissionNode;
    }

    /**
     * @return whether this permission is author exclusive.
     */
    public boolean isAuthorExclusive() {
        return isAuthorExclusive;
    }

    /**
     * @return whether this permission is granted by default (if no other permission entry covers it).
     */
    public boolean isDefault() {
        return isDefault;
    }
}
