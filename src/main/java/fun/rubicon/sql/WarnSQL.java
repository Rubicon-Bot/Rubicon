package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.Warn;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class WarnSQL implements SQLHandler {

    private Connection connection;
    private MySQL mySQL;

    public WarnSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public List<Warn> getWarns(User user, Guild guild) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM warns WHERE warnedUser = ? AND serverid=?");
            ps.setString(1, user.getId());
            ps.setString(2, guild.getId());
            ResultSet rs = ps.executeQuery();
            List<Warn> warns = new ArrayList<>();
            while (rs.next()) {
                warns.add(Warn.parseWarn(
                        rs.getString("id"),
                        rs.getString("warnedUser"),
                        rs.getString("serverid"),
                        rs.getString("executor"),
                        rs.getString("reason"),
                        rs.getString("date")));
            }
            return warns;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return new ArrayList<>();
    }

    public void addWarn(Warn warn) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO warns VALUES (0, ?, ?, ?, ?, ?)");
            ps.setString(1, warn.getWarnedUser().getId());
            ps.setString(2, warn.getGuild().getId());
            ps.setString(3, warn.getExecutor().getId());
            ps.setString(4, warn.getReason());
            ps.setString(5, String.valueOf(warn.getDate()));
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public void deleteWarn(User user, Guild guild, int index) {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM warns WHERE id=?");
            ps.setInt(1, getWarns(user, guild).get(index).getId());
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
                    "CREATE TABLE IF NOT EXISTS `warns` (" +
                    "  `id` INT(11) NOT NULL AUTO_INCREMENT," +
                    "  `warnedUser` VARCHAR(50) NOT NULL," +
                    "  `serverid` VARCHAR(50) NOT NULL," +
                    "  `executor` VARCHAR(50) NOT NULL," +
                    "  `reason` TEXT NOT NULL," +
                    "  `date` VARCHAR(100) NOT NULL," +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
