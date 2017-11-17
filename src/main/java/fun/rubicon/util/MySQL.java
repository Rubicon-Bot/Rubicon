package fun.rubicon.util;

import net.dv8tion.jda.core.entities.Guild;

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
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`) VALUES (?, '0', 'LE!', 'Welcome %user% on %guild%', 'Bye %user%', '0')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}
