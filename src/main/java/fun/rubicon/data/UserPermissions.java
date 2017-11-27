/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.data;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.Arrays;

/**
 * Member/User-specific object used to query all permission-relevant variables.
 *
 * @author tr808axm
 */
public class UserPermissions {
    private final long userId;
    private final long guildId;

    private int memberPermissionLevel;
    private boolean isBotAuthor;
    private boolean isServerOwner;
    private boolean isAdministrator;
    private String[] memberPermissionNodes;

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

    private void update() {
        // prepare data sources
        Member discordMember = getDiscordMember();


        // update member permission level
        memberPermissionLevel = 0;
        if (discordMember != null) { // memberPermissionLevel stays 0 in private message
            try {
                memberPermissionLevel = Integer.parseInt(RubiconBot.getMySQL()
                        .getMemberValue(getDiscordMember(), "permissionlevel"));
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
        isAdministrator = discordMember != null && discordMember.getPermissions().contains(Permission.ADMINISTRATOR);

        // update member permissions
        memberPermissionNodes = discordMember == null
                ? new String[0]
                : RubiconBot.getMySQL().getMemberValue(discordMember, "permissions").split(",");
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
     */
    public int getMemberPermissionLevel() {
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
        return memberPermissionNodes;
    }

    /**
     * @param requiredPermissionNode the required permission node.
     * @return whether memberPermissionNodes contains requiredPermissionNode.
     */
    public boolean hasPermissionNode(String requiredPermissionNode) {
        return Arrays.asList(memberPermissionNodes).contains(requiredPermissionNode);
    }
}