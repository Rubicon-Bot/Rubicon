/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.permission;

import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
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
public class PermissionManager extends RethinkHelper {

    private Rethink rethink;
    private Table table;

    public PermissionManager() {
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("permissions");

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
        table.insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                        .with("type", String.valueOf(target.getType().getIdentifier()))
                        .with("id", String.valueOf(target.getId()))
                        .with("permission", permission.getPermissionString())
                        .with("negated", permission.isNegated())
        )).run(rethink.connection);
        return true;
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
        Cursor cursor = ignoreNegation ? table.filter(
                rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                .with("type", String.valueOf(target.getType().getIdentifier()))
                .with("id", String.valueOf(target.getId()))
                .with("permission", permission.getPermissionString())
        ).run(rethink.connection) :
                table.filter(rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                        .with("type", String.valueOf(target.getType().getIdentifier()))
                        .with("id", String.valueOf(target.getId()))
                        .with("permission", permission.getPermissionString())
                        .with("negated", permission.isNegated())
                ).run(rethink.connection);
        return exist(cursor);
    }

    /**
     * Loads a {@link Permission} object from the database.
     *
     * @param target           the target to query.
     * @param permissionString the permission to query.
     * @return the {@link Permission Permission object} with a negation value or null if it does not exist.
     */
    public Permission getPermission(PermissionTarget target, String permissionString) {
        Cursor cursor = table.filter(rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                .with("type", String.valueOf(target.getType().getIdentifier()))
                .with("id", String.valueOf(target.getId()))
                .with("permission", permissionString)
        ).run(rethink.connection);
        List list = cursor.toList();
        return list.size() == 1
                ? new Permission(permissionString, (boolean) ((Map) list.get(0)).get("negated")) // entry with negation value
                : null; // no entry
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
        table.filter(
                rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                        .with("type", String.valueOf(target.getType().getIdentifier()))
                        .with("id", String.valueOf(target.getId()))
                        .with("permission", permission.getPermissionString())
        ).delete().run(rethink.connection);
        return true;
    }

    public List<Permission> getPermissions(PermissionTarget target) {
        Cursor cursor = table.filter(
                rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                        .with("type", String.valueOf(target.getType().getIdentifier()))
                        .with("id", String.valueOf(target.getId()))
        ).run(rethink.connection);
        List<Permission> targetPermissions = new ArrayList<>();
        for (Object obj : cursor) {
            Map map = (Map) obj;
            targetPermissions.add(new Permission((String) map.get("permission"), (boolean) map.get("negated")));
        }
        return targetPermissions;
    }

    /**
     * Fetches all permission entries for a guild.
     *
     * @param guild the guild whose permission entries should be fetched.
     * @return all permissions grouped by their target.
     */
    public Map<PermissionTarget, List<Permission>> getGuildPermissions(Guild guild) {
        Cursor cursor = table.filter(row -> row
                .g("guildId").eq(guild.getId())
        ).run(rethink.connection);
        Map<PermissionTarget, List<Permission>> guildPermissions = new HashMap<>();
        for (Object obj : cursor) {
            Map map = (Map) obj;
            // construct target (key)
            PermissionTarget target = new PermissionTarget(guild,
                    PermissionTarget.Type.getByIdentifier(((String) map.get("type")).charAt(0)),
                    Long.valueOf((String) map.get("id")));
            // add target entry if necessary
            if (!guildPermissions.containsKey(target))
                guildPermissions.put(target, new ArrayList<>());
            // add permission
            guildPermissions.get(target).add(new Permission((String) map.get("permission"),
                    (boolean) map.get("negated")));
        }
        return guildPermissions;
    }
}