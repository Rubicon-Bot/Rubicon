/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a music_users table-row.
 * @author Yannick Seeger / ForYaSee
 */
public class UserMusicSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final User user;

    /**
     * Initializes this database entity and creates it if it does not exist.
     * @param mySQL the database.
     * @param user the user.
     */
    public UserMusicSQL(MySQL mySQL, User user) {
        this.mySQL = mySQL;
        this.user = user;

        if (user != null)
            create();
    }

    /**
     * @deprecated Use {@link #UserMusicSQL(MySQL, User)} instead.
     */
    @Deprecated
    public UserMusicSQL(User user) {
        this(RubiconBot.getMySQL(), user);
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `music_users` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in music_users entry for user "
                    + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `music_users` SET " + type + " = ? WHERE `userid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, user.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in music_users entry for user "
                    + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `music_users` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking music_users entry existence for user " + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        try {
            PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                    "INSERT INTO `music_users` (`userid`) VALUES (?);");
            insertStatement.setString(1, user.getId());
            insertStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while creating music_users entry for user " + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `music_users` (\n" +
                        "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `userid` VARCHAR(50) NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static UserMusicSQL generatorInstance(MySQL mySQL) {
        return new UserMusicSQL(mySQL, null);
    }
}