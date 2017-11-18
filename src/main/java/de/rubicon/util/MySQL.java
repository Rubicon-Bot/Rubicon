package de.rubicon.util;

import net.dv8tion.jda.core.entities.Guild;
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

    /**
     * @param table
     * @param key
     * @param where
     * @param wherevalue
     * @return Value of the given key
     */
    public String getString(String table, String key,String where, String wherevalue) {
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

    public MySQL generatePermissions(Guild guild){
        guild.getMembers().forEach(u -> {
            try{
                PreparedStatement ps = connection.prepareStatement("INSERT INTO `permissions`(`discordid`, `serverid`, `permlvl`) VALUES (?, ?, '0');");
                ps.setString(1, u.getUser().getId());
                ps.setString(2, guild.getId());
                ps.execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        });
        return this;
    }

    public MySQL createUserPermissiones(User user, Guild guild){
        try{
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `permissions`(`discordid`, `serverid`, `permlvl`) VALUES (?, ?, '0');");
            ps.setString(1, user.getId());
            ps.setString(2, guild.getId());
            ps.execute();
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

    public boolean ifGuildExits(Guild guild){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guild where serverid =?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public MySQL updateGuildValue(Guild guild, String row, String value){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guild`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, 'logchannel') VALUES (?, '0', '0', '0', '_', '1', '0', 'Welcome %user% on %guild%', 'Bye %user%', '0')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return this;
    }



}
