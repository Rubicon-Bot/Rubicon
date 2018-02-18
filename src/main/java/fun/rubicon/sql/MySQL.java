/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private static Connection connection;
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;

    /**
     * @return MySQL connection
     * Use MySQL.getCon() instead
     */
    @Deprecated
    public static Connection getConnection() {
        return connection;
    }

    /**
     * @param host     Host of MySQL server
     * @param port     Port of MySQL server
     * @param user     User of MySQL database
     * @param password Password of MySQL user
     * @param dbname   Name of MySQL database
     */
    public MySQL(String host, String port, String user, String password, String dbname) throws NullPointerException {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = dbname;
    }

    /**
     * @return MySQL connection
     */
    public MySQL connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", this.user, this.password);
            Logger.info("MySQL connection success");
        } catch (SQLException e) {
            Logger.error(e);
            Logger.error("MySQL connection failed");
            Logger.info("Shutdown application...");
            System.exit(1);
        }
        return this;
    }

    public MySQL disconnect() {
        try {
            connection.close();
            System.out.println("disconnected from MYSQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Connection getCon(){ return this.connection; }

    /**
     * @param table
     * @param key
     * @param where
     * @param wherevalue
     * @return Value of the given key
     */
    public String getString(String table, String key, String where, String wherevalue) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, where);
            ps.setString(3, wherevalue);
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param table      Tablename
     * @param key        column name
     * @param value      value
     * @param where
     * @param wherevalue
     * @return null
     */
    public MySQL setString(String table, String key, String value, String where, String wherevalue) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE ? SET ?=? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, key);
            ps.setString(3, value);
            ps.setString(4, where);
            ps.setString(5, wherevalue);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public MySQL executePreparedStatement(PreparedStatement ps) {
        try {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL executePreparedStatements(PreparedStatement... statements) {
        for (PreparedStatement statement : statements) {
            try {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    //Role Stuff
    public boolean ifRoleExist(Role role) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles WHERE roleid = ?");
            ps.setString(1, role.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updateRoleValue(Role role, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifRoleExist(role))
                createRole(role);
            PreparedStatement ps = connection.prepareStatement("UPDATE roles SET " + type + " = '" + value + "' WHERE role = '" + role.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getRoleValue(Role role, String type) {
        createRoleIfNecessary(role);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles WHERE `roleid` = ?");
            ps.setString(1, role.getId());
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a Role if it is not already in the database. Used to ensure data.
     *
     * @param role the Role to check and create.
     */
    private MySQL createRoleIfNecessary(Role role) {
        if (!ifRoleExist(role))
            createRole(role);
        return this;
    }

    public MySQL createRole(Role role) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `roles`(`roleid`, `permissions`) VALUES (?, '')");
            ps.setString(1, String.valueOf(role.getId()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    //Portal Stuff
    public boolean ifPortalExist(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM portal WHERE guildid = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updatePortalValue(Guild guild, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE portal SET " + type + " = '" + value + "' WHERE guildid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getPortalValue(Guild guild, String type) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM portal WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MySQL createPortal(Guild guild, Guild otherguild, TextChannel channel) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `portal`(`guildid`, `partnerid`, `channelid`) VALUES (?, ?,?)");
            ps.setString(1, String.valueOf(guild.getId()));
            ps.setString(2, String.valueOf(otherguild.getId()));
            ps.setString(3, String.valueOf(channel.getId()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL deletePortal(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `portal` WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    //Guild Stuff
    public List<Guild> getGuildsByValue(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds where `" + type + "` = ?");
            ps.setString(1, value);
            List<Guild> guilds = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                guilds.add(RubiconBot.getJDA().getGuildById(rs.getString("serverid")));
            }
            return guilds;
        } catch (SQLException ex) {
            Logger.error(ex);
        }
        return null;
    }

    public boolean ifGuildExits(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid =?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updateGuildValue(Guild guild, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifGuildExits(guild))
                createGuildServer(guild);
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET " + type + " = '" + value + "' WHERE serverid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getGuildValue(Guild guild, String type) {
        createGuildIfNecessary(guild);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            // Only returning one result
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a guild if it is not already in the database. Used to ensure data.
     *
     * @param guild the Guild to check and create.
     */
    private MySQL createGuildIfNecessary(Guild guild) {
        if (!ifGuildExits(guild))
            createGuildServer(guild);
        return this;
    }

    /**
     * @see GuildSQL
     */
    @Deprecated
    public MySQL createGuildServer(Guild guild) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`,`lvlmsg`, `whitelist`) VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '','1', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL createGuildServer(String serverID) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`) VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '')");
            ps.setString(1, serverID);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL deleteGuild(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `guilds` WHERE `serverid` = ?");
            ps.setString(1, guild.getId());
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `members` WHERE `serverid` = ?");
            ps2.setString(1, guild.getId());
            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL deleteGuild(String serverID) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `guilds` WHERE `serverid` = ?");
            ps.setString(1, serverID);
            ps.execute();
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `members` WHERE `serverid` = ?");
            ps2.setString(1, serverID);
            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL deleteGuildVerification(Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `verifications` WHERE `guildid` =?");
            ps.setString(1, g.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getVerificationValue(Guild g, String key) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MySQL createVerification(CommandVerification.VerificationSettings settings) {
        String kicktext = "0";
        if (settings.kicktext != null)
            kicktext = settings.kicktext;
        String emote;
        if (settings.emote.getId() != null)
            emote = settings.emote.getId();
        else
            emote = settings.emote.getName();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifications` (`guildid`, `channelid`, `roleid`, `text`, `verifiedtext`, `kicktime`, `kicktext`, `emote`) VALUES ( ?, ?, ?,?,?,?,?,?);");
            ps.setString(1, settings.channel.getGuild().getId());
            ps.setString(2, settings.channel.getId());
            ps.setString(3, settings.role.getId());
            ps.setString(4, settings.verifytext);
            ps.setString(5, settings.verifiedtext);
            ps.setString(6, String.valueOf(settings.kicktime));
            ps.setString(7, kicktext);
            ps.setString(8, emote);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean verificationEnabled(Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWhitelisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").equals("") || rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    public boolean isChannelWhitelisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error(e);
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    public boolean isBlacklisted(TextChannel channel) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {

        }
        return false;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }
}
