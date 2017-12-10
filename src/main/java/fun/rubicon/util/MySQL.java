package fun.rubicon.util;

import fun.rubicon.core.DiscordCore;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Yannick Seeger
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */

public class MySQL {

    private static Connection connection;
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;

    public static Connection getConnection() {
        return connection;
    }

    /**
     * @param host
     * @param port
     * @param user
     * @param password
     * @param dbname
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

    public void updateRoleValue(Role role, String type, String value) {
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
    private void createRoleIfNecessary(Role role) {
        if (!ifRoleExist(role))
            createRole(role);
    }

    public void createRole(Role role) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `roles`(`roleid`, `permissions`) VALUES (?, '')");
            ps.setString(1, String.valueOf(role.getId()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void updateMemberValue(Member member, String type, String value) {
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
    private void createMemberIfNecessary(Member member) {
        if (!ifMemberExist(member))
            createMember(member);
    }

    public void createMember(Member member) {
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

    public void updatePortalValue(Guild guild, String type, String value) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE portal SET " + type + " = '" + value + "' WHERE guildid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void createPortal(Guild guild, Guild otherguild, TextChannel channel) {
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
    }

    public void deletePortal(Guild guild) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `portal` WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void updateUserValue(User user, String type, String value) {
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
    private void createUserIfNecessary(User user) {
        if (!ifUserExist(user))
            createUser(user);
    }

    public void createUser(User user) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `user`(`userid`, `bio`, `bday`, `level`, `points`, `money`) VALUES (?, 0, 0, 0, 0, 0)");
            ps.setString(1, String.valueOf(user.getIdLong()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Guild Stuff
    public List<Guild> getGuildsByValue(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds where `" + type + "` = ?");
            ps.setString(1, value);
            List<Guild> guilds = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                guilds.add(DiscordCore.getJDA().getGuildById(rs.getString("serverid")));
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
                    guilds.add(DiscordCore.getJDA().getGuildById(rs.getString("serverid")));
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

    public void updateGuildValue(Guild guild, String type, String value) {
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
    private void createGuildIfNecessary(Guild guild) {
        if (!ifGuildExits(guild))
            createGuildServer(guild);
    }

    public void createGuildServer(Guild guild) {
        try {
            if (connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`) VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGuild(Guild guild) {
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
    }

    public void createWarning(Guild guild, User target, User author, String reason) {
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

    public void deleteWarning(User user, Guild g) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `warnings` WHERE `userid` = ? AND `serverid` = ?");
            ps.setString(1, user.getId());
            ps.setString(2, g.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
