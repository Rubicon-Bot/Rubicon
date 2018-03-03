/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a portal table-row.
 * TODO database generator
 * @author tr808axm
 */
public class PortalSQL implements DatabaseEntry {
    private final MySQL database;
    private final Guild guild;

    /**
     * Initializes this database entity.
     * @param database the database.
     * @param guild the guild.
     */
    public PortalSQL(MySQL database, Guild guild) {
        this.database = database;
        this.guild = guild;
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `portal` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking portal entry existence for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Do not use, use {@link #create(Guild, TextChannel)} instead.
     */
    @Override
    public void create() {
        throw new UnsupportedOperationException("Cannot create a portal entry without");
    }

    /**
     * Creates a portal entry.
     * @param otherGuild the server to connect to.
     * @param portalTextChannel the text channel to stream.
     */
    public void create(Guild otherGuild, TextChannel portalTextChannel) {
        try {
            PreparedStatement insertStatement = database.getActiveConnection().prepareStatement(
                    "INSERT INTO `portal` (`guildid`, `partnerid`, `channelid`) VALUES (?, ?, ?);");
            insertStatement.setString(1, guild.getId());
            insertStatement.setString(2, otherGuild.getId());
            insertStatement.setString(3, portalTextChannel.getId());
            insertStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while creating portal entry for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Deletes this entry from the database and disables message-forwarding by thus.
     */
    public void delete() {
        try {
            PreparedStatement deleteStatement = database.getActiveConnection().prepareStatement(
                    "DELETE FROM `portal` WHERE `guildid` = ?;");
            deleteStatement.setString(1, guild.getId());
            deleteStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while deleting a portal entry for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `portal` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in portal entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = database.getActiveConnection().prepareStatement(
                    "UPDATE `portal` SET " + type + " = ? WHERE `guildid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, guild.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in portal entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }
}
