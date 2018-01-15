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
public class GuildMusicSQL implements SQLHandler {

    private Guild guild;
    private Connection connection;
    private MySQL mySQL;

    /**
     * Used for database generation
     */
    public GuildMusicSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public GuildMusicSQL(Guild guild) {
        this.guild = guild;
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public String get(String type) {
        createDefaultEntryIfNotExist();
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM music_guilds WHERE `guildid` = ?");
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
        createDefaultEntryIfNotExist();
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("UPDATE music_guilds SET " + type + "=? WHERE guildid=?");
            ps.setString(1, value);
            ps.setString(2, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public void createDefaultEntryIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM music_guilds WHERE guildid = ?");
            checkStatement.setString(1, guild.getId());
            ResultSet checkResult = checkStatement.executeQuery();
            if (checkResult.next())
                return;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO music_guilds (guildid, dj, locked_channel) VALUES (?, 'false', 'false')");
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
                    "CREATE TABLE IF NOT EXISTS `music_guilds` (\n" +
                    " `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                    " `guildid` VARCHAR(50) NOT NULL,\n" +
                    " `dj` VARCHAR(50) NOT NULL,\n" +
                    " `locked_channel` VARCHAR(50) NOT NULL,\n" +
                    " PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
