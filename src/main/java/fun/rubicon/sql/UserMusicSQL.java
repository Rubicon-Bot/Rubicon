package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class UserMusicSQL implements SQLHandler {

    private User user;
    private Connection connection;
    private MySQL mySQL;

    /**
     * Used for databse generation
     */
    public UserMusicSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public UserMusicSQL(User user) {
        this.user = user;
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public String get(String type) {
        createDefaultEntryIfNotExist();
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM music_users WHERE `userid` = ?");
            ps.setString(1, user.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(type);
            }
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }


    public void set(String type, String value) {
        createDefaultEntryIfNotExist();
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE music_users SET " + type + "=? WHERE userid=?");
            ps.setString(1, value);
            ps.setString(2, user.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public void createDefaultEntryIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM music_users WHERE userid = ?");
            checkStatement.setString(1, user.getId());
            ResultSet checkResult = checkStatement.executeQuery();
            if (checkResult.next())
                return;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO music_users (userid) VALUES (?)");
            ps.setString(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("" +
                    "CREATE TABLE IF NOT EXISTS `music_users` (\n" +
                    "  `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `userid` VARCHAR(50) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}