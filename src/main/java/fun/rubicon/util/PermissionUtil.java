/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class PermissionUtil {

    public static boolean canManageMessages(Member member, Channel channel) {
        return member.getPermissions(channel).contains(Permission.MESSAGE_MANAGE);
    }
}
