/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a guilds table-row.
 */
public class GuildSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final Guild guild;

    /**
     * Initializes this database entity.
     * @param mySQL the database.
     * @param guild the guild.
     */
    public GuildSQL(MySQL mySQL, Guild guild) {
        this.mySQL = mySQL;
        this.guild = guild;

        if(guild != null)
            create();
    }

    /**
     * @deprecated Use {@link #GuildSQL(MySQL, Guild)} instead.
     */
    @Deprecated
    private GuildSQL(Guild guild, MySQL mySQL) {
        this(RubiconBot.getMySQL(), guild);
    }

    /**
     * @deprecated Use {@link #GuildSQL(MySQL, Guild)} instead.
     */
    @Deprecated
    public static GuildSQL fromGuild(Guild guild) {
        return new GuildSQL(guild, RubiconBot.getMySQL());
    }

    /**
     * @return whether the whitelist is enabled on this guild.
     */
    public boolean isWhitelistEnabled() {
        String value = get("whitelist");
        return value != null && !value.isEmpty();
    }

    /**
     * @deprecated Use {@link #isWhitelistEnabled()} instead.
     */
    @Deprecated
    public boolean enabledWhitelist(){
        return isWhitelistEnabled();
    }

    /**
     * @return whether the blacklist is enabled.
     */
    public boolean isBlacklistEnabled() {
        String value = get("blacklist");
        return value != null && !value.isEmpty();
    }

    /**
     * @deprecated Use {@link #isBlacklistEnabled()} instead.
     */
    @Deprecated
    public boolean enabledBlacklist(){
        return isBlacklistEnabled();
    }

    /**
     * Checks whether a {@link TextChannel} is blacklisted.
     * @param channel the text channel to be checked.
     * @return whether the channel is blacklisted.
     */
    public boolean isBlacklisted(TextChannel channel){
        return get("blacklist").contains(channel.getId());
    }

    /**
     * Checks whether a {@link TextChannel} is blacklisted.
     * @param channel the text channel to be checked.
     * @return whether the channel is blacklisted.
     */
    public boolean isWhitelisted(TextChannel channel){
        return get("whitelist").contains(channel.getId());
    }

    /**
     * Queries the guilds table for guilds with a specified value.
     * @param database
     * @param jda
     * @param columnName
     * @param value
     * @return
     */
    public static List<Guild> getByValue(MySQL database, JDA jda, String columnName, String value) {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `guilds` WHERE `" + columnName + "` = ?;");
            selectStatement.setString(1, value);
            List<Guild> guilds = new ArrayList<>();
            ResultSet selectResult = selectStatement.executeQuery();
            while (selectResult.next())
                guilds.add(jda.getGuildById(selectResult.getString("serverid")));
            return guilds;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving guilds entries with '" + columnName + "' = '" + value + "':");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `guilds` WHERE `serverid` = ?;");
            selectStatement.setString(1, guild.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in guilds entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            create();
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `guilds` SET " + type + " = ? WHERE serverid = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, guild.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in guilds entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `guilds` WHERE `serverid` = ?;");
            selectStatement.setString(1, guild.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking guilds entry existence for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        if (!exists()) {
            try {
                PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                        "INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, " +
                                "`autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`,`lvlmsg`, `whitelist`) " +
                                "VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', " +
                                "'0', '', '','1', '');");
                insertStatement.setString(1, guild.getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating guilds entry for guild " + guild.getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }
        }
    }

    public void deleteIncludingMembers() {
        delete();
        try {
            PreparedStatement deleteStatement = mySQL.getActiveConnection().prepareStatement(
                    "DELETE FROM `members` WHERE `serverid` = ?;");
            deleteStatement.setString(1, guild.getId());
            deleteStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while deleting all members entries for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Deletes this entry and removes all guild data by thus. Member data is still stored in the members table, use
     * {@link #deleteIncludingMembers()} to wipe out members data as well.
     */
    private void delete() {
        try {
            PreparedStatement deleteStatement = mySQL.getActiveConnection().prepareStatement(
                    "DELETE FROM `guilds` WHERE `serverid` = ?;");
            deleteStatement.setString(1, guild.getId());
            deleteStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while deleting a guilds entry for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Table creation script.
     * @throws SQLException if any sql error occurs.
     */
    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `guilds`" +
                "(`serverid` VARCHAR(100) , " +
                "`prefix` VARCHAR (25)," +
                "`joinmsg` TEXT," +
                "`leavemsg` TEXT," +
                "`channel` TEXT," +
                "`logchannel` TEXT," +
                "`autorole` TEXT," +
                "`portal` VARCHAR (250)," +
                "`welmsg` TEXT," +
                "`autochannels` VARCHAR (250)," +
                "`cases` INT (11)," +
                "`blacklist` TEXT," +
                "`lvlmsg` INT (11)," +
                "`whitelist` TEXT," +
                " PRIMARY KEY (`serverid`)" +
                ") ENGINE=InnoDB;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static GuildSQL generatorInstance(MySQL mySQL) {
        return new GuildSQL(mySQL, null);
    }
}
