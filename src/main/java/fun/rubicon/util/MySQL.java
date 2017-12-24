/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.admin.CommandVerification;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

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
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * @param host Host of MySQL server
     * @param port Port of MySQL server
     * @param user User of MySQL database
     * @param password Password of MySQL user
     * @param dbname Name of MySQL database
     */
    public MySQL(String host, String port, String user, String password, String dbname) {
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

    public MySQL executePreparedStatements(PreparedStatement... statements){
        for(PreparedStatement statement : statements){
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

    //Member Stuff
    public boolean ifMemberExist(Member member) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM member WHERE userid = ? AND guildid = ?");
            ps.setString(1, member.getUser().getId());
            ps.setString(2, member.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updateMemberValue(Member member, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifMemberExist(member))
                createMember(member);
            PreparedStatement ps = connection.prepareStatement("UPDATE member SET " + type + " = '" + value + "' WHERE userid = '" + member.getUser().getId() + "' AND guildid = '" + member.getGuild().getId() + "'");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getMemberValue(Member member, String type) {
        createMemberIfNecessary(member);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM member WHERE `userid` = ? AND `guildid` = ?");
            ps.setString(1, member.getUser().getId());
            ps.setString(2, member.getGuild().getId());
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
     * Creates a Member if it is not already in the database. Used to ensure data.
     *
     * @param member the Member to check and create.
     */
    private MySQL createMemberIfNecessary(Member member) {
        if (!ifMemberExist(member))
            createMember(member);
        return this;
    }

    public MySQL createMember(Member member) {
        try {
            if (connection.isClosed())
                connect();
            String permLevel;
            if (member.isOwner()) {
                permLevel = "3";
            } else if (member.getPermissions().contains(Permission.ADMINISTRATOR)) {
                permLevel = "2";
            } else {
                permLevel = "1";
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `member`(`userid`, `guildid`, `permissionlevel`, `permissions`) VALUES (?, ?, ?, '')");
            ps.setString(1, String.valueOf(member.getUser().getId()));
            ps.setString(2, String.valueOf(member.getGuild().getId()));
            ps.setString(3, String.valueOf(permLevel));
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


    //User Stuff
    public boolean ifUserExist(User user) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE userid = ?");
            ps.setString(1, user.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updateUserValue(User user, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            if (!ifUserExist(user))
                createUser(user);
            PreparedStatement ps = connection.prepareStatement("UPDATE user SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getUserValue(User user, String type) {
        createUserIfNecessary(user);
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE `userid` = ?");
            ps.setString(1, user.getId());
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
     * Creates a User if it is not already in the database. Used to ensure data.
     *
     * @param user the User to check and create.
     */
    private MySQL createUserIfNecessary(User user) {
        if (!ifUserExist(user))
            createUser(user);
        return this;
    }

    public MySQL createUser(User user) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `user`(`userid`, `bio`, `bday`, `level`, `points`, `money`) VALUES (?, 0, 0, 0, 0, 0)");
            ps.setString(1, String.valueOf(user.getIdLong()));
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

    public List<Guild> getGuildsByContainingValue(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds");
            List<Guild> guilds = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString(type).contains(value)) {
                    guilds.add(RubiconBot.getJDA().getGuildById(rs.getString("serverid")));
                }
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

    public MySQL createGuildServer(Guild guild) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`) VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
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
            PreparedStatement ps2 = connection.prepareStatement("DELETE FROM `member` WHERE `guildid` = ?");
            ps2.setString(1, guild.getId());
            ps2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL createWarning(Guild guild, User target, User author, String reason) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `warnings`(`serverid`,`userid`,`authorid`,`reason`) VALUES (?,?,?,?)");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.setString(2, String.valueOf(target.getId()));
            ps.setString(3, String.valueOf(author.getId()));
            ps.setString(4, reason);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getWarning(User user, Guild guild, String type) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM warnings WHERE `userid` = ? AND `serverid` = ?");
            ps.setString(1, user.getId());
            ps.setString(2, guild.getId());
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

    public boolean ifWarning(User user, Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM warnings WHERE userid = ? AND serverid = ?");
            ps.setString(1, user.getId());
            ps.setString(2, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MySQL deleteWarning(User user, Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `warnings` WHERE `userid` = ? AND `serverid` = ?");
            ps.setString(1, user.getId());
            ps.setString(2, g.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MySQL deleteGuildVerification(Guild g){
        try{
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `verifications` WHERE `guildid` =?");
            ps.setString(1, g.getId());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return this;
    }

    public String getVerificationValue(Guild g, String key){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(key);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public MySQL createVerification(CommandVerification.VerificationSettings settings){
        String kicktext = "0";
        if(settings.kicktext != null)
            kicktext = settings.kicktext;
        String emote;
        if(settings.emote.getId() != null)
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

        } catch (SQLException e){
            e.printStackTrace();
        }
        return this;
    }

    public boolean verificationEnabled(Guild g){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `verifications` WHERE `guildid` = ?");
            ps.setString(1, g.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBlacklisted(TextChannel channel){
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, channel.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString("blacklist").contains(channel.getId());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
