package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificationUserSQL implements DatabaseGenerator {

    private Connection connection;
    private MySQL mySQL;
    private Guild guild;
    private Member member;

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public VerificationUserSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();

    }

    public Message getMessage() {
        return guild.getTextChannelById(RubiconBot.getMySQL().getVerificationValue(guild, "channelid")).getMessageById(Long.parseLong(this.get("messageid"))).complete();
    }

    public VerificationUserSQL(Guild guild, Member member) {
        this.connection = MySQL.getConnection();
        this.mySQL = RubiconBot.getMySQL();
        this.guild = guild;
        this.member = member;
    }

    public static VerificationUserSQL fromMember(Member member) {

        return new VerificationUserSQL(member.getGuild(), member);
    }

    @Override
    public void createTableIfNotExist() {


        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`verifyusers`" +
                    "(`id` INT NOT NULL AUTO_INCREMENT, " +
                    "`guildid` TEXT NOT NULL, " +
                    "`userid` TEXT NOT NULL, " +
                    "`messageid` TEXT NOT NULL," +
                    "PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifyusers WHERE userid = ?");
            ps.setString(1, this.getMember().getUser().getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void set(String type, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE verifyusers SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, this.getMember().getUser().getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String type) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM verifyusers WHERE `userid` = ?");
            ps.setString(1, this.getMember().getUser().getId());
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

    public boolean insert() {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `verifyusers` (`guildid`, `userid`, `messageid`) VALUES (?,?,?)");
            ps.setLong(1, this.getGuild().getIdLong());
            ps.setLong(2, this.getMember().getUser().getIdLong());
            ps.setLong(3, this.getMessage().getIdLong());
            return true;
        } catch (SQLException e) {
            Logger.error(e);
            return false;
        }
    }
}
