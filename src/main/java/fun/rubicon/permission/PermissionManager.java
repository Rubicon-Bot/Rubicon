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

import java.sql.SQLException;
import java.util.ArrayList;
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
        List<Permission> permissions = getPermissions(target);
        permissions.add(permission);
        updatePermissions(target, permissions);
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
        List<Permission> permissions = getPermissions(target);
        if (permissions.contains(permission))
            return true;
        if (!ignoreNegation)
            return false;
        for (Permission p : permissions) {
            if (p.getPermissionString().equals(permission.getPermissionString().replace("!", "")))
                return true;
        }
        return false;
    }

    /**
     * Removes a permission entry.
     *
     * @param target     the permission target.
     * @param permission the permission to remove.
     * @return {@code false} if there was no entry for {@code permission} and {@code target}.
     */
    public boolean removePermission(PermissionTarget target, Permission permission) {
        List<Permission> permissions = getPermissions(target);
        permissions.remove(permission);
        updatePermissions(target, permissions);
        return true;
    }

    public List<Permission> getPermissions(PermissionTarget target) {
        List<Permission> permissions = new ArrayList<>();
        Cursor cursor = table.filter(
                rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                        .with("type", String.valueOf(target.getType().getIdentifier()))
                        .with("id", String.valueOf(target.getId()))
        ).run(rethink.getConnection());
        try {
            Map<?, ?> map = (Map<?, ?>) cursor.toList().get(0);
            List<String> rawList = (List<String>) map.get("permissions");
            for (String raw : rawList) {
                permissions.add(Permission.parse(raw));
            }
        } catch (Exception ignore) {

        }
        return permissions;
    }

    private void updatePermissions(PermissionTarget target, List<Permission> permissions) {
        List<String> res = new ArrayList<>();
        for (Permission permission : permissions)
            res.add(permission.toString());
        if (getPermissions(target).size() == 0) {
            table.filter(rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                    .with("type", String.valueOf(target.getType().getIdentifier()))
                    .with("id", String.valueOf(target.getId()))).delete().run(rethink.getConnection());//TODO Do this better
            table.insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                    .with("type", String.valueOf(target.getType().getIdentifier()))
                    .with("id", String.valueOf(target.getId()))
                    .with("permissions", res))).run(rethink.getConnection());
        } else {
            table.filter(rethink.rethinkDB.hashMap("guildId", target.getGuild().getId())
                    .with("type", String.valueOf(target.getType().getIdentifier()))
                    .with("id", String.valueOf(target.getId()))).update(
                    rethink.rethinkDB.hashMap("permissions", res)).run(rethink.getConnection());
        }
    }
}