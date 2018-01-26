/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.data;

import fun.rubicon.RubiconBot;
import fun.rubicon.permission.Permission;
import fun.rubicon.sql.MemberSQL;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Member/User-specific object used to query all permission-relevant variables.
 *
 * @author tr808axm
 * @deprecated Use the {@link fun.rubicon.permission} package instead.
 */
@Deprecated
public class UserPermissions extends fun.rubicon.permission.UserPermissions {
    private PermissionLevel memberPermissionLevel;

    public UserPermissions(long userId, long guildId) {
        super(userId, guildId);
    }

    public UserPermissions(long userId) {
        super(userId);
    }

    public UserPermissions(User user, Guild guild) {
        super(user, guild);
    }

    public UserPermissions(User user) {
        super(user);
    }

    /**
     * Updates all permission-related values.
     */
    public void update() {
        // prepare data sources
        Member discordMember = getDiscordMember();

        // update member permission level
        memberPermissionLevel = PermissionLevel.EVERYONE;
        if (discordMember != null) { // memberPermissionLevel stays 0 in private message
            try {
                memberPermissionLevel = PermissionLevel.getByValue(Integer.parseInt(new MemberSQL(discordMember).get("permissionlevel")));
            } catch (NumberFormatException e) {
                Logger.error("Permission value for user " + getUserId() + " in guild " + getGuildId()
                        + " is not an integer number.");
            }
        }
    }

    /**
     * @return an array of all permission nodes the user has on the specified guild. Also is an empty array (length 0)
     * if no guild was specified.
     */
    public String[] getMemberPermissionNodes() {
        return getEffectivePermissions().stream().map(Permission::getPermissionString).toArray(String[]::new);
    }

    /**
     * @return the member permission level. Also 0 if no guild was specified.
     * @see fun.rubicon.permission.PermissionManager for usage definitions.
     * @deprecated use {@link #getNewMemberPermissionLevel().value} instead.
     */
    @Deprecated
    public int getMemberPermissionLevel() {
        return memberPermissionLevel.value;
    }

    /**
     * @return the member permission level. {@code null} if no guild was specified.
     */
    public PermissionLevel getNewMemberPermissionLevel() {
        return memberPermissionLevel;
    }
}