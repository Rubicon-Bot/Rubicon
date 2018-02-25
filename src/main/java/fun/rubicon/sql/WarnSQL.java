/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Warn;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the warns database table.
 * @author Yannick Seeger / ForYaSee
 */
public class WarnSQL implements DatabaseGenerator {
    private MySQL database;

    /**
     * Initializes this interface.
     * @param database
     */
    public WarnSQL(MySQL database) {
        this.database = database;
    }

    /**
     * @deprecated Use {@link #WarnSQL(MySQL)} instead.
     */
    @Deprecated
    public WarnSQL() {
        this(RubiconBot.getMySQL());
    }

    /**
     * Collects a list of warns for a specific user on a specific guild.
     * @param user the warned user.
     * @param guild the guild where the user was warned.
     * @return a list object containing all warns.
     */
    public List<Warn> getWarns(User user, Guild guild) {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `warns` WHERE `warnedUser` = ? AND `serverid` = ?;");
            selectStatement.setString(1, user.getId());
            selectStatement.setString(2, guild.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            List<Warn> warns = new ArrayList<>();
            while (selectResult.next())
                warns.add(Warn.parseWarn(
                        selectResult.getString("id"),
                        selectResult.getString("warnedUser"),
                        selectResult.getString("serverid"),
                        selectResult.getString("executor"),
                        selectResult.getString("reason"),
                        selectResult.getString("date")));
            return warns;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving warns entries for user " + user.getId() + " in guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Saves a warn object to the warns table.
     * @param warn the warn to be saved.
     */
    public void addWarn(Warn warn) {
        try {
            PreparedStatement insertStatement = database.getActiveConnection().prepareStatement(
                    "INSERT INTO `warns` VALUES (0, ?, ?, ?, ?, ?);");
            insertStatement.setString(1, warn.getWarnedUser().getId());
            insertStatement.setString(2, warn.getGuild().getId());
            insertStatement.setString(3, warn.getExecutor().getId());
            insertStatement.setString(4, warn.getReason());
            insertStatement.setString(5, String.valueOf(warn.getDate()));
            insertStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while adding a warns entry for user " + warn.getWarnedUser().getId()
                    + " in guild " + warn.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Deletes a warn entry from the warns table.
     * @param user the warned user.
     * @param guild the guild where the user was warned.
     * @param index the warn index. TODO: Clarify: What is the index?
     */
    public void deleteWarn(User user, Guild guild, int index) {
        try {
            PreparedStatement deleteStatement = database.getActiveConnection().prepareStatement(
                    "DELETE FROM `warns` WHERE `id` = ?;");
            deleteStatement.setInt(1, getWarns(user, guild).get(index).getId());
            deleteStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while deleting a warns entry for user " + user.getId() + " in guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        database.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `warns` (" +
                        "  `id` INT(11) NOT NULL AUTO_INCREMENT," +
                        "  `warnedUser` VARCHAR(50) NOT NULL," +
                        "  `serverid` VARCHAR(50) NOT NULL," +
                        "  `executor` VARCHAR(50) NOT NULL," +
                        "  `reason` TEXT NOT NULL," +
                        "  `date` VARCHAR(100) NOT NULL," +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        ).execute();
    }
}
