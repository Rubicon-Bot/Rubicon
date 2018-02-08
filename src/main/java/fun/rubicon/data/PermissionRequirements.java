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
 * @deprecated Use the {@link fun.rubicon.permission} package instead.
 */
@Deprecated
public class PermissionRequirements extends fun.rubicon.permission.PermissionRequirements {
    public PermissionRequirements(PermissionLevel requiredPermissionLevel, String requiredPermissionNode) {
        super(requiredPermissionNode,
                requiredPermissionLevel == PermissionLevel.BOT_AUTHOR,
                requiredPermissionLevel == PermissionLevel.EVERYONE);
    }

    public PermissionRequirements(int requiredPermissionLevel, String requiredPermissionNode) {
        this(PermissionLevel.getByValue(requiredPermissionLevel), requiredPermissionNode);
    }
}
