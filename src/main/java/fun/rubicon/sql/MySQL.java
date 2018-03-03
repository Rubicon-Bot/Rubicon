/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.sound.sampled.Port;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database interface.
 */
public class MySQL {
    private Connection connection;
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final String database;

    /**
     * Initializes this database interface but does not connect it.
     * @param host MySQL server host
     * @param port MySQL server port
     * @param user MySQL database user
     * @param password MySQL database user password
     * @param database name of the MySQL database (schema)
     * @see #connect() for establishing a connection and
     * @see #getActiveConnection() for getting a connected connection.
     */
    public MySQL(String host, String port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    /**
     * Establishes a {@link Connection}.
     * @see #getActiveConnection() to get the connection.
     * @return this MySQL object.
     */
    public MySQL connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", this.user, this.password);
            Logger.info("MySQL connection success");
        } catch (SQLException e) {
            Logger.error("MySQL connection failed, application will terminate.");
            Logger.error(e);
            System.exit(1);
        }
        return this;
    }

    /**
     * Disconnects this interface from the database.
     * @return this MySQL object.
     */
    public MySQL disconnect() {
        try {
            connection.close();
            System.out.println("disconnected from MYSQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Utility method for reading a cell from any table that can be identified with one WHERE clause.
     * @param tableName the table to query.
     * @param columnName the column to query.
     * @param whereColumnName the column used in the WHERE clause.
     * @param whereValue the value used in the WHERE clause.
     * @return String content of the first result.
     * @throws RuntimeException on any database exception.
     */
    public String getString(String tableName, String columnName, String whereColumnName, String whereValue) {
        try {
            PreparedStatement selectStatement = getActiveConnection().prepareStatement(
                    "SELECT * FROM ? WHERE ? = ?;");
            selectStatement.setString(1, tableName);
            selectStatement.setString(2, whereColumnName);
            selectStatement.setString(3, whereValue);
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(columnName) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + columnName + "' value in " + tableName
                    + " table for identifier '" + whereColumnName + "' = '" + whereValue + "':");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
    }

    /**
     * Utility method for updating a cell from any table that can be identified with one WHERE clause.
     * @param tableName the table to update.
     * @param columnName the column to update.
     * @param newValue the new cell value.
     * @param whereColumnName the column used in the WHERE clause.
     * @param whereValue the value used in the WHERE clause.
     * @return this MySQL object.
     * @throws RuntimeException on any database exception.
     */
    public MySQL setString(String tableName, String columnName, String newValue, String whereColumnName, String whereValue) {
        try {
            PreparedStatement updateStatement = getActiveConnection().prepareStatement(
                    "UPDATE ? SET ? = ? WHERE ? = ?;");
            updateStatement.setString(1, tableName);
            updateStatement.setString(2, columnName);
            updateStatement.setString(3, newValue);
            updateStatement.setString(4, whereColumnName);
            updateStatement.setString(5, whereValue);
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + columnName + "' value in " + tableName
                    + " table for identifier '" + whereColumnName + "' = '" + whereValue + "':");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.", e);
        }
        return this;
    }

    /**
     * @return The database connection object without checking it.
     * @see #getActiveConnection() to get a connection that has been checked.
     */
    public Connection getUnsafeConnection() {
        return connection;
    }

    /**
     * @return a connected {@link Connection}.
     * @throws SQLException if connecting the connection failed.
     */
    public Connection getActiveConnection() throws SQLException {
        if (connection.isClosed())
            connect();
        return connection;
    }

    //Role Stuff

    /**
     * @deprecated Use {@link RoleSQL#exists()} instead.
     */
    @Deprecated
    public boolean ifRoleExist(Role role) {
        return new RoleSQL(this, role).exists();
    }

    /**
     * @deprecated Use {@link RoleSQL#set(String, String)} instead.
     */
    @Deprecated
    public MySQL updateRoleValue(Role role, String type, String value) {
        RoleSQL roleSQL = new RoleSQL(this, role);
        roleSQL.create();
        roleSQL.set(type, value);
        return this;
    }

    /**
     * @deprecated Use {@link RoleSQL#get(String)} instead.
     */
    @Deprecated
    public String getRoleValue(Role role, String type) {
        RoleSQL roleSQL = new RoleSQL(this, role);
        roleSQL.create();
        return roleSQL.get(type);
    }

    /**
     * @deprecated Use {@link RoleSQL#create()} instead.
     */
    private MySQL createRoleIfNecessary(Role role) {
        new RoleSQL(this, role).create();
        return this;
    }

    /**
     * @deprecated Use {@link RoleSQL#create()} instead.
     */
    public MySQL createRole(Role role) {
        new RoleSQL(this, role).create();
        return this;
    }

    //Portal Stuff

    /**
     * @deprecated Use {@link PortalSQL#exists()} instead.
     */
    @Deprecated
    public boolean ifPortalExist(Guild guild) {
        return new PortalSQL(this, guild).exists();
    }

    /**
     * @deprecated Use {@link PortalSQL#set(String, String)} instead.
     */
    @Deprecated
    public MySQL updatePortalValue(Guild guild, String type, String value) {
        new PortalSQL(this, guild).set(type, value);
        return this;
    }

    /**
     * @deprecated Use {@link PortalSQL#get(String)} instead.
     */
    @Deprecated
    public String getPortalValue(Guild guild, String type) {
        return new PortalSQL(this, guild).get(type);
    }

    /**
     * @deprecated Use {@link PortalSQL#create(Guild, TextChannel)} instead.
     */
    @Deprecated
    public MySQL createPortal(Guild guild, Guild otherguild, TextChannel channel) {
        new PortalSQL(this, guild).create(otherguild, channel);
        return this;
    }

    /**
     * @deprecated Use {@link PortalSQL#delete()} instead.
     */
    @Deprecated
    public MySQL deletePortal(Guild guild) {
        new PortalSQL(this, guild).delete();
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#getByValue(MySQL, JDA, String, String)} instead.
     */
    @Deprecated
    public List<Guild> getGuildsByValue(String type, String value) {
        return GuildSQL.getByValue(this, RubiconBot.getJDA(), type, value);
    }

    /**
     * @deprecated Use {@link #getActiveConnection()} instead.
     */
    @Deprecated
    public static Connection getConnection() {
        return RubiconBot.getMySQL().getUnsafeConnection();
    }

    /**
     * @deprecated Use {@link GuildSQL#exists()} instead.
     */
    @Deprecated
    public boolean ifGuildExits(Guild guild) {
        return  new GuildSQL(this, guild).exists();
    }

    /**
     * @deprecated Use {@link GuildSQL#set(String, String)}.
     */
    @Deprecated
    public MySQL updateGuildValue(Guild guild, String type, String value) {
        GuildSQL guildSQL = new GuildSQL(this, guild);
        guildSQL.create();
        guildSQL.set(type, value);
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#get(String)} instead, but remember to.
     */
    @Deprecated
    public String getGuildValue(Guild guild, String type) {
        GuildSQL guildSQL = new GuildSQL(this, guild);
        guildSQL.create();
        return guildSQL.get(type);
    }

    /**
     * @deprecated Use {@link GuildSQL#create()} instead.
     */
    @Deprecated
    private MySQL createGuildIfNecessary(Guild guild) {
        new GuildSQL(this, guild).create();
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#create()} instead.
     */
    @Deprecated
    public MySQL createGuildServer(Guild guild) {
        new GuildSQL(this, guild).create();
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#create()} instead.
     */
    @Deprecated
    public MySQL createGuildServer(String serverID) {
        new GuildSQL(this, RubiconBot.getJDA().getGuildById(serverID)).create();
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#deleteIncludingMembers()} instead.
     */
    @Deprecated
    public MySQL deleteGuild(Guild guild) {
        new GuildSQL(this, guild).deleteIncludingMembers();
        return this;
    }

    /**
     * @deprecated Use {@link GuildSQL#deleteIncludingMembers()} instead.
     */
    @Deprecated
    public MySQL deleteGuild(String serverID) {
        new GuildSQL(this, RubiconBot.getJDA().getGuildById(serverID)).deleteIncludingMembers();
        return this;
    }

    /**
     * @deprecated Use {@link VerificationSQL#get(String)} instead.
     */
    @Deprecated
    public String getVerificationValue(Guild g, String key) {
        return new VerificationSQL(this, g).get(key);
    }

    /**
     * @deprecated Use {@link VerificationSQL#create()} instead.
     */
    @Deprecated
    public MySQL createVerification(CommandVerification.VerificationSettings settings) {
        new VerificationSQL(this, settings.channel.getGuild()).create(settings);
        return this;
    }

    /**
     * @deprecated Use {@link VerificationSQL#isEnabled()} instead.
     */
    @Deprecated
    public boolean verificationEnabled(Guild g) {
        try {
            return new VerificationSQL(this, g).isEnabled();
        } catch (RuntimeException e) {
            return false;
        }
    }
}
