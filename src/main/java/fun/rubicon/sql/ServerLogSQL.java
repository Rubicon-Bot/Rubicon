package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class ServerLogSQL implements DatabaseGenerator {
    private Guild guild;
    private Connection connection;
    private MySQL mySQL;

    /**
     * Uses for database generation
     */
    public ServerLogSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public ServerLogSQL(Guild guild) {
        this.guild = guild;
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();

        create();
    }

    public String get(String type) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM serverlog WHERE `guildid` = ?");
            ps.setString(1, guild.getId());
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
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE serverlog SET " + type + "=? WHERE guildid=?");
            ps.setString(1, value);
            ps.setString(2, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public void create() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM serverlog WHERE guildid = ?");
            checkStatement.setString(1, guild.getId());
            ResultSet checkResult = checkStatement.executeQuery();
            if (checkResult.next())
                return;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO serverlog (guildid, channel, ev_join, ev_leave, ev_command, ev_ban, ev_voice, ev_role) VALUES (?, '0', 'false', 'false', 'false', 'false', 'false', 'false')");
            ps.setString(1, guild.getId());
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
                    "CREATE TABLE IF NOT EXISTS `serverlog` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `guildid` varchar(50) NOT NULL,\n" +
                    "  `channel` varchar(50) NOT NULL,\n" +
                    "  `ev_join` varchar(50) NOT NULL,\n" +
                    "  `ev_leave` varchar(50) NOT NULL,\n" +
                    "  `ev_command` varchar(50) NOT NULL,\n" +
                    "  `ev_ban` varchar(50) NOT NULL,\n" +
                    "  `ev_voice` varchar(50) NOT NULL,\n" +
                    "  `ev_role` varchar(50) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
