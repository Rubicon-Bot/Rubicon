/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class ServerLogSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final Guild guild;

    /**
     * Initializes this database entity and creates the database entry.
     * @param mySQL the database.
     * @param guild the guild.
     */
    public ServerLogSQL(MySQL mySQL, Guild guild) {
        this.mySQL = mySQL;
        this.guild = guild;

        if(guild != null)
            create();
    }

    /**
     * @deprecated Use {@link #ServerLogSQL(MySQL, Guild)} instead.
     */
    @Deprecated
    public ServerLogSQL(Guild guild) {
        this(RubiconBot.getMySQL(), guild);
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `serverlog` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in serverlog entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `serverlog` SET " + type + " = ? WHERE `guildid` = ?;");
            insertStatement.setString(1, value);
            insertStatement.setString(2, guild.getId());
            insertStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in serverlog entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `serverlog` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking serverlog entry existence for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        if (!exists()) {
            try {
                PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                        "INSERT INTO serverlog (guildid, channel, ev_join, ev_leave, ev_command, ev_ban, ev_voice, ev_role) " +
                                "VALUES (?, '0', 'false', 'false', 'false', 'false', 'false', 'false')");
                insertStatement.setString(1, guild.getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating serverlog entry for guild " + guild.getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }
        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `serverlog` (\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                        "  `guildid` varchar(50) NOT NULL,\n" +
                        "  `channel` varchar(50) NOT NULL,\n" +
                        "  `ev_join` varchar(50) NOT NULL,\n" +
                        "  `ev_leave` varchar(50) NOT NULL,\n" +
                        "  `ev_command` varchar(50) NOT NULL,\n" +
                        "  `ev_ban` varchar(50) NOT NULL,\n" +
                        "  `ev_voice` varchar(50) NOT NULL,\n" +
                        "  `ev_role` varchar(50) NOT NULL,\n" +
                        "  PRIMARY KEY (`id`)\n" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static ServerLogSQL generatorInstance(MySQL mySQL) {
        return new ServerLogSQL(mySQL, null);
    }
}
