/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a roles table-row.
 * TODO database generator
 * @author tr808axm
 */
public class RoleSQL implements DatabaseEntry {
    private final MySQL database;
    private final Role role;

    /**
     * Initializes this database entity.
     * @param database the database.
     * @param role the role.
     */
    public RoleSQL(MySQL database, Role role) {
        this.database = database;
        this.role = role;
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `roles` WHERE `roleid` = ?;");
            selectStatement.setString(1, role.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking roles entry existence for role " + role.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
    }

    @Override
    public void create() {
        if (!exists()) {
            try {
                PreparedStatement insertStatement = database.getActiveConnection().prepareStatement(
                        "INSERT INTO `roles`(`roleid`, `permissions`) VALUES (?, '')");
                insertStatement.setString(1, role.getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating roles entry for role " + role.getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }
        }
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `roles` WHERE `roleid` = ?;");
            selectStatement.setString(1, role.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in roles entry for role "
                    + role.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = database.getActiveConnection().prepareStatement(
                    "UPDATE `roles` SET " + type + " = ? WHERE `roleid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, role.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in roles entry for role "
                    + role.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
    }
}
