package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificationKickSQL implements DatabaseGenerator {

    private Connection connection;
    private MySQL mySQL;
    private User user;
    private Guild guild;

    /**
     * Uses for database generation
     */
    public VerificationKickSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    public VerificationKickSQL(User user, Guild guild) {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
        this.user = user;
        this.guild = guild;

    }

    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifykicks WHERE userid = ?");
            ps.setString(1, user.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void set(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE verifykicks SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String type) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifykicks WHERE `userid` = ?");
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


    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`verifykicks` " +
                    "( `id` INT NOT NULL AUTO_INCREMENT ," +
                    " `guildid` TEXT NOT NULL ," +
                    " `userid` TEXT NOT NULL ," +
                    " `kickText` TEXT NOT NULL ," +
                    " `kickTime` TEXT NOT NULL," +
                    " `message` TEXT NOT NULL, " +
                    " PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
}
