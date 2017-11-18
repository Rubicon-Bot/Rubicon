package fun.rubicon.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;

/**
 * @author Schlaubi
 * @version 1.0
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
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @param dbname
     *
     */
    public MySQL(String host, String port, String user, String password, String dbname){
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = dbname;
    }

    /**
     *
     * @return MySQL connection
     */
    public MySQL connect(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
            Logger.info("MySQL connection success");
        } catch (SQLException e){
            Logger.error("MySQL connection failed");
            e.printStackTrace();
        }
        return this;
    }
    public MySQL disconnect(){
        try {
            connection.close();
            System.out.println("disconnected from MYSQL");
        } catch (SQLException e){
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
    public String etString(String table, String key,String where, String wherevalue) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, where);
            ps.setString(3, wherevalue);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                return rs.getString(key);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param table
     * @param key
     * @param value
     * @param where
     * @param wherevalue
     * @return null
     */
    public MySQL setString(String table, String key, String value, String where, String wherevalue){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE ? SET ?=? WHERE ?=?");
            ps.setString(1, table);
            ps.setString(2, key);
            ps.setString(3, value);
            ps.setString(4, where);
            ps.setString(5, wherevalue);
        } catch (SQLException e){
            e.printStackTrace();
        }

        return this;
    }

    public MySQL executePreparedStatement(PreparedStatement ps){
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
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles where roleid = ?");
            ps.setString(1, role.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void updateRoleValue(Role role, String type, String value){
        try{
            if(connection.isClosed())
                connect();
            if(!ifRoleExist(role))
                createRole(role);
            PreparedStatement ps = connection.prepareStatement("UPDATE roles SET " + type + " = '" + value + "' WHERE role = '" + role.getId());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String getRoleValue(Role role, String type) {
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM roles WHERE `roleid` = ?");
            ps.setString(1, role.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString(type);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void createRole(Role role) {
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `roles`(`roleid`, `permissions`) VALUES (?, '')");
            ps.setString(1, String.valueOf(role.getId()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //Member Stuff
    public boolean ifMemberExist(Member member) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM member where userid = ? AND guildid = ?");
            ps.setString(1, member.getUser().getId());
            ps.setString(2, member.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void updateMemberValue(Member member, String type, String value){
        try{
            if(connection.isClosed())
                connect();
            if(!ifMemberExist(member))
                createMember(member);
            PreparedStatement ps = connection.prepareStatement("UPDATE member SET " + type + " = '" + value + "' WHERE userid = '" + member.getUser().getId() + "' AND guildid = '" + member.getGuild().getId() + "'");
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String getMemberValue(Member member, String type){
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM member WHERE `userid` = ? AND `guildid` = ?");
            ps.setString(1, member.getUser().getId());
            ps.setString(2, member.getGuild().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString(type);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void createMember(Member member) {
        try{
            if(connection.isClosed())
                connect();
            String permLevel = (member.isOwner()) ? "3" : "0";
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `member`(`userid`, `guildid`, `permissionlevel`, `permissions`) VALUES (?, ?, ?, '')");
            ps.setString(1, String.valueOf(member.getUser().getId()));
            ps.setString(2, String.valueOf(member.getGuild().getId()));
            ps.setString(3, String.valueOf(permLevel));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //User Stuff
    public boolean ifUserExist(User user) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM user where userid = ?");
            ps.setString(1, user.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void updateUserValue(User user, String type, String value){
        try{
            if(connection.isClosed())
                connect();
            if(!ifUserExist(user))
                createUser(user);
            PreparedStatement ps = connection.prepareStatement("UPDATE user SET " + type + " = '" + value + "' WHERE userid = " + user.getId());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String getUserValue(User user, String type){
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM user WHERE `userid` = ?");
            ps.setString(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString(type);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void createUser(User user) {
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `user`(`userid`, `bio`) VALUES (?, '')");
            ps.setString(1, String.valueOf(user.getIdLong()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //Guild Stuff
    public boolean ifGuildExits(Guild guild){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds where serverid =?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void updateGuildValue(Guild guild, String type, String value){
        try{
            if(connection.isClosed())
                connect();
            if(!ifGuildExits(guild))
                createGuildServer(guild);
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET " + type + " = '" + value + "' WHERE serverid = " + guild.getId());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String getGuildValue(Guild guild, String type){
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE `serverid` = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getString(type);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public void createGuildServer(Guild guild) {
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`) VALUES (?, '0', 'LE!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

}
