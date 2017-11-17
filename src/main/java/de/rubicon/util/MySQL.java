package de.rubicon.util;

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
            Logger.error("MySQL connection failes");
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
}
