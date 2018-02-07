/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import fun.rubicon.RubiconBot;
import fun.rubicon.sql.MySQL;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the rubicon permission system.
 *
 * @author tr808axm
 */
public class PermissionManager {
    private static final String TABLE = "permissions-v1";

    public PermissionManager() {
        // ensure table existence
        try {
            MySQL.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + TABLE + "` (" +
                            "`guildid` BIGINT SIGNED, " +
                            "`type` CHAR(1), " +
                            "`id` BIGINT SIGNED, " +
                            "`permission` VARCHAR(300)," +
                            "`negated` BOOLEAN" +
                            ");")
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException("Could not create permissions table.", e);
        }

        RubiconBot.getCommandManager().registerCommandHandler(new PermissionCommandHandler());
    }

    /**
     * Adds a permission entry.
     *
     * @param target     the permission target.
     * @param permission the permission to add.
     * @return {@code false} if there already is an entry for {@code permission} and {@code target}.
     */
    public boolean addPermission(PermissionTarget target, Permission permission) {
        if (hasPermission(target, permission, true))
            return false;
        try {
            PreparedStatement insertStatement = MySQL.getConnection()
                    .prepareStatement("INSERT INTO `" + TABLE + "` " +
                            "(`guildid`, `type`, `id`, `permission`, `negated`) VALUES (?, ?, ?, ?, ?)");
            insertStatement.setLong(1, target.getGuild().getIdLong());
            insertStatement.setString(2, String.valueOf(target.getType().getIdentifier()));
            insertStatement.setLong(3, target.getId());
            insertStatement.setString(4, permission.getPermissionString());
            insertStatement.setBoolean(5, permission.isNegated());
            insertStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error has occurred while saving data to the database.", e);
        }
    }

    /**
     * Checks whether there is a permission entry for a specific permission and target.
     *
     * @param target         the permission target.
     * @param permission     the permission to check.
     * @param ignoreNegation whether negation should be ignored.
     * @return {@code true} if there is an entry and {@code false} otherwise.
     * @throws RuntimeException in case of an {@link SQLException}.
     */
    public boolean hasPermission(PermissionTarget target, Permission permission, boolean ignoreNegation) {
        try {
            PreparedStatement selectStatement = MySQL.getConnection()
                    .prepareStatement("SELECT * FROM `" + TABLE + "` " +
                            "WHERE `guildid` = ? " +
                            "AND `type` = ? " +
                            "AND `id` = ? " +
                            "AND `permission` = ? " +
                            (!ignoreNegation ? "AND `negated` = ?;" : ";"));
            selectStatement.setLong(1, target.getGuild().getIdLong());
            selectStatement.setString(2, String.valueOf(target.getType().getIdentifier()));
            selectStatement.setLong(3, target.getId());
            selectStatement.setString(4, permission.getPermissionString());
            if (!ignoreNegation)
                selectStatement.setBoolean(5, permission.isNegated());
            return selectStatement.executeQuery().next(); // has permission if there is an entry
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error has occurred while fetching database information.", e);
        }
    }

    /**
     * Loads a {@link Permission} object from the database.
     *
     * @param target           the target to query.
     * @param permissionString the permission to query.
     * @return the {@link Permission Permission object} with a negation value or null if it does not exist.
     */
    public Permission getPermission(PermissionTarget target, String permissionString) {
        try {
            PreparedStatement selectStatement = MySQL.getConnection()
                    .prepareStatement("SELECT `negated` FROM `" + TABLE + "` " +
                            "WHERE `guildid` = ? " +
                            "AND `type` = ? " +
                            "AND `id` = ? " +
                            "AND `permission` = ?;");
            selectStatement.setLong(1, target.getGuild().getIdLong());
            selectStatement.setString(2, String.valueOf(target.getType().getIdentifier()));
            selectStatement.setLong(3, target.getId());
            selectStatement.setString(4, permissionString);
            ResultSet queryResult = selectStatement.executeQuery();
            return queryResult.next()
                    ? new Permission(permissionString, queryResult.getBoolean("negated")) // entry with negation value
                    : null; // no entry
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error has occurred while fetching database information.", e);
        }
    }

    /**
     * Removes a permission entry.
     *
     * @param target     the permission target.
     * @param permission the permission to remove.
     * @return {@code false} if there was no entry for {@code permission} and {@code target}.
     */
    public boolean removePermission(PermissionTarget target, Permission permission) {
        if (!hasPermission(target, permission, true))
            return false;
        try {
            PreparedStatement deleteStatement = MySQL.getConnection()
                    .prepareStatement("DELETE FROM `" + TABLE + "` " +
                            "WHERE `guildid` = ? " +
                            "AND `type` = ? " +
                            "AND `id` = ? " +
                            "AND `permission` = ?;");
            deleteStatement.setLong(1, target.getGuild().getIdLong());
            deleteStatement.setString(2, String.valueOf(target.getType().getIdentifier()));
            deleteStatement.setLong(3, target.getId());
            deleteStatement.setString(4, permission.getPermissionString());
            deleteStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error has occurred while saving data to the database.", e);
        }
    }

    public List<Permission> getPermissions(PermissionTarget target) {
        try {
            PreparedStatement selectStatement = MySQL.getConnection()
                    .prepareStatement("SELECT `permission`, `negated` FROM `" + TABLE + "` " +
                            "WHERE `guildid` = ? " +
                            "AND `type` = ? " +
                            "AND `id` = ?;");
            selectStatement.setLong(1, target.getGuild().getIdLong());
            selectStatement.setString(2, String.valueOf(target.getType().getIdentifier()));
            selectStatement.setLong(3, target.getId());
            ResultSet queryResult = selectStatement.executeQuery();
            List<Permission> targetPermissions = new ArrayList<>();
            while (queryResult.next())
                targetPermissions.add(new Permission(queryResult.getString("permission"),
                        queryResult.getBoolean("negated")));
            return targetPermissions;
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error occurred while fetching data from the database.", e);
        }
    }

    /**
     * Fetches all permission entries for a guild.
     *
     * @param guild the guild whose permission entries should be fetched.
     * @return all permissions grouped by their target.
     */
    public Map<PermissionTarget, List<Permission>> getGuildPermissions(Guild guild) {
        try {
            PreparedStatement selectStatement = MySQL.getConnection()
                    .prepareStatement("SELECT `type`, `id`, `permission`, `negated` FROM `" + TABLE + "` WHERE `guildid` = ?;");
            selectStatement.setLong(1, guild.getIdLong());
            ResultSet queryResult = selectStatement.executeQuery();

            Map<PermissionTarget, List<Permission>> guildPermissions = new HashMap<>();
            while (queryResult.next()) {
                // construct target (key)
                PermissionTarget target = new PermissionTarget(guild,
                        PermissionTarget.Type.getByIdentifier(queryResult.getString("type").charAt(0)),
                        queryResult.getLong("id"));
                // add target entry if necessary
                if (!guildPermissions.containsKey(target))
                    guildPermissions.put(target, new ArrayList<>());
                // add permission
                guildPermissions.get(target).add(new Permission(queryResult.getString("permission"),
                        queryResult.getBoolean("negated")));
            }
            return guildPermissions;
        } catch (SQLException e) {
            throw new RuntimeException("An unknown error occurred while fetching data from the database.", e);
        }
    }
}
