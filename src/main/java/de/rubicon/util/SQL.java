package de.rubicon.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.*;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 17.11.2017 20:14
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class SQL {
    private static Connection connection;
    private static String password = Info.MYSQL_PASSWORD;

    public static void connect(){
        if(!isConnected()){
            try{

                String host = Info.MYSQL_HOST;
                String port = Info.MYSQL_PORT;
                String database = Info.MYSQL_DATABASE;
                String username = Info.MYSQL_USER;
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
                System.out.println("Lee´s MYSQL started");

            } catch (SQLException e) {
                System.out.println("Lee´s MYSQL failed to start");
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected(){
        return (connection != null);
    }


    public static boolean ifGuildExists(Guild guild){
        try {
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guild WHERE serverid = ?");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public static void updateValue(Guild guild, String type, String value){
        try{
            if(connection.isClosed())
                connect();
            if(!ifGuildExists(guild))
                createServer(guild);
            PreparedStatement ps = connection.prepareStatement("UPDATE guild SET " + type + " = '" + value + "' WHERE serverid = " + guild.getId());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public static void createServer(Guild guild){
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guild`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, 'logchannel') VALUES (?, '0', '0', '0', '_', '1', '0', 'Welcome %user% on %guild%', 'Bye %user%', '0')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static String getValue(Guild guild, String type){
        try{
            if(connection.isClosed())
                connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guild WHERE `serverid` = ?");
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
}
