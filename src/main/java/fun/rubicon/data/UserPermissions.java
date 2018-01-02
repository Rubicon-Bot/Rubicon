/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.data;

import fun.rubicon.RubiconBot;
import fun.rubicon.permission.Permission;
import fun.rubicon.permission.PermissionManager;
import fun.rubicon.permission.PermissionTarget;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Member/User-specific object used to query all permission-relevant variables.
 *
 * @author tr808axm
 */
public class UserPermissions {
    private final long userId;
    private final long guildId;

    private PermissionLevel memberPermissionLevel;
    private boolean isBotAuthor;
    private boolean isServerOwner;
    private boolean isAdministrator;

    /**
     * Construct a UserPermissions object.
     *
     * @param userId  the long id of the user whose permissions are reviewed.
     * @param guildId the id of the guild on which the permissions should apply.
     */
    public UserPermissions(long userId, long guildId) {
        this.userId = userId;
        this.guildId = guildId;
        update();
    }

    /**
     * Construct a UserPermissions object without a guild.
     *
     * @param userId the long id of the user whose permissions are reviewed.
     */
    public UserPermissions(long userId) {
        this(userId, -1);
    }

    /**
     * Convenience constructor
     *
     * @param user  the user whose permissions are reviewed.
     * @param guild the guild on which the permissions should apply. If this is null, no guild will be specified.
     */
    public UserPermissions(User user, Guild guild) {
        this(user.getIdLong(), guild == null ? -1 : guild.getIdLong());
    }

    /**
     * Convenience constructor without a guild.
     *
     * @param user the user whose permissions are reviewed.
     */
    public UserPermissions(User user) {
        this(user, null);
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
                memberPermissionLevel = PermissionLevel.getByValue(Integer.parseInt(RubiconBot.getMySQL()
                        .getMemberValue(getDiscordMember(), "permissionlevel")));
            } catch (NumberFormatException e) {
                Logger.error("Permission value for user " + userId + " in guild " + guildId
                        + " is not an integer number.");
            }
        }

        // update is bot owner
        isBotAuthor = Arrays.asList(Info.BOT_AUTHOR_IDS).contains(userId);

        // update is server owner
        isServerOwner = discordMember != null && discordMember.isOwner();

        // update is administrator
        isAdministrator = discordMember != null && discordMember.getPermissions()
                .contains(net.dv8tion.jda.core.Permission.ADMINISTRATOR);
    }

    /**
     * Convenience method returning the corresponding user, if available.
     *
     * @return the {@link User} corresponding to userId or null if this is not available.
     */
    public User getDiscordUser() {
        return RubiconBot.getJDA() == null ? null : RubiconBot.getJDA().getUserById(userId);
    }

    /**
     * Convenience method returning the corresponding guild, if available.
     *
     * @return the {@link Guild} corresponding to guildId or null if this is not available.
     */
    public Guild getDiscordGuild() {
        return RubiconBot.getJDA() == null ? null : RubiconBot.getJDA().getGuildById(guildId);
    }

    /**
     * Convenience method returning the corresponding member, if available.
     *
     * @return the {@link net.dv8tion.jda.core.entities.Member} corresponding to userId and guildId or null if this is
     * not available.
     */
    public Member getDiscordMember() {
        User discordUser = getDiscordUser();
        Guild discordGuild = getDiscordGuild();
        return discordUser == null || discordGuild == null ? null : discordGuild.getMember(discordUser);
    }

    /**
     * @return the user id this object works with.
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @return the guild id this object works with. -1 if no guild was specified.
     */
    public long getGuildId() {
        return guildId;
    }

    /**
     * @return the member permission level. Also 0 if no guild was specified.
     * @see fun.rubicon.core.permission.PermissionManager for usage definitions.
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

    /**
     * @return whether the user is a bot author. Independent from guilds.
     */
    public boolean isBotAuthor() {
        return isBotAuthor;
    }

    /**
     * @return whether the user is the owner of the specified guild. Also false if no guild was specified.
     */
    public boolean isServerOwner() {
        return isServerOwner;
    }

    /**
     * @return whether the user has the ADMINISTRATOR permission on the specified guild. Also false if no guild was
     * specified.
     */
    public boolean isAdministrator() {
        return isAdministrator;
    }

    /**
     * @return an array of all permission nodes the user has on the specified guild. Also is an empty array (length 0)
     * if no guild was specified.
     */
    public String[] getMemberPermissionNodes() {
        PermissionManager manager = RubiconBot.sGetPermissionManager();
        List<Permission> effectivePermissions = new ArrayList<>();
        for (PermissionTarget target : getPermissionTargets(null))
            for(Permission targetPermission : manager.getPermissions(target))
                // only add to effective if there is no equal permission string yet.
                if(effectivePermissions.stream()
                        .noneMatch(effectivePermission -> effectivePermission.equalsIgnoreNegation(targetPermission)))
                    effectivePermissions.add(targetPermission);
        List<String> effectivePermissionStrings = effectivePermissions.stream().map(Permission::getPermissionString).collect(Collectors.toList());
        return effectivePermissionStrings.toArray(new String[effectivePermissionStrings.size()]);
    }

    /**
     * @param requiredPermissionNode the required permission node.
     * @return whether memberPermissionNodes contains requiredPermissionNode.
     */
    public boolean hasPermissionNode(String requiredPermissionNode) {
        return hasPermission(null, requiredPermissionNode);
    }

    /**
     * @param context used to check discord permissions in a channel.
     * @param requiredPermissionNode the required permission node.
     * @return whether memberPermissionNodes contains requiredPermissionNode.
     */
    public boolean hasPermission(Channel context, String requiredPermissionNode) {
        Permission permission = getEffectivePermissionEntry(context, requiredPermissionNode);
        // negated -> false (does not have perm), not negated -> true (has perm)
        return permission != null && !permission.isNegated();
    }

    /**
     * Iterates through all {@link PermissionTarget PermissionTargets} and returns the effective {@link Permission} entry.
     * @param context used to check discord permissions in a channel.
     * @param requiredPermissionNode the permission to query.
     * @return the effect
     */
    public Permission getEffectivePermissionEntry(Channel context, String requiredPermissionNode) {
        PermissionManager permissionManager = RubiconBot.sGetPermissionManager();
        Permission effectivePermissionEntry = null;
        // check permissions
        List<PermissionTarget> permissionTargets = getPermissionTargets(context);
        for(int i = 0; effectivePermissionEntry == null && i < permissionTargets.size(); i++) {
            Permission permission = permissionManager.getPermission(permissionTargets.get(i), requiredPermissionNode);
            if (permission != null)
                effectivePermissionEntry = permission;
        }
        return effectivePermissionEntry;
    }

    /**
     * @param context used to check discord permissions in a channel.
     * @return all {@link PermissionTarget PermissionTargets} that apply on this user in the order they should be
     * checked.
     */
    public List<PermissionTarget> getPermissionTargets(Channel context) {
        List<PermissionTarget> targets = new ArrayList<>();
        if (isMember()) {
            Member member = getDiscordMember();

            // add user target
            targets.add(new PermissionTarget(member));

            // add discord permission targets
            for(net.dv8tion.jda.core.Permission permission : context == null ? member.getPermissions() : member.getPermissions(context))
                targets.add(new PermissionTarget(member.getGuild(), permission));

            // add role targets
            List<Role> roleList = member.getRoles(); // member roles sorted from highest to lowest
            roleList.forEach(role -> targets.add(new PermissionTarget(role))); // add all roles
        }
        return targets;
    }

    /**
     * @return whether this permission object can access member permission settings.
     */
    public boolean isMember() {
        return guildId != -1;
    }
}