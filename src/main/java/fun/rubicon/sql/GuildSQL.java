package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSQL implements DatabaseGenerator{

    private Connection connection;
    private MySQL mySQL;
    private Guild guild;

    @Override
    public void createTableIfNotExist() {
        try {
            PreparedStatement ps = RubiconBot.getMySQL().getCon().prepareStatement("CREATE TABLE IF NOT EXISTS `guilds`" +
                    "(`serverid` VARCHAR(100) , " +
                    "`prefix` VARCHAR (25)," +
                    "`joinmsg` TEXT," +
                    "`leavemsg` TEXT," +
                    "`channel` TEXT," +
                    "`logchannel` TEXT," +
                    "`autorole` TEXT," +
                    "`portal` VARCHAR (250)," +
                    "`welmsg` TEXT," +
                    "`autochannels` VARCHAR (250)," +
                    "`cases` INT (11)," +
                    "`blacklist` TEXT," +
                    "`lvlmsg` INT (11)," +
                    "`whitelist` TEXT)," +
                    " PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
        private GuildSQL(Guild guild, MySQL mySQL) {
            this.guild = guild;
            this.mySQL = RubiconBot.getMySQL();
            this.connection = MySQL.getConnection();
        }

        public static GuildSQL fromGuild(Guild guild) {
            return new GuildSQL(guild, RubiconBot.getMySQL());
        }


    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid = ?");
            ps.setString(1, guild.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void set(String type, String value) {
        try {
            if (!exist())
                create();
            PreparedStatement ps = connection.prepareStatement("UPDATE guilds SET " + type + " = '" + value + "' WHERE serverid = ?");
            ps.setString(1, guild.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void create() {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO `guilds`(`serverid`, `channel`, `prefix`, `joinmsg`, `leavemsg`, `logchannel`, `autorole`, `portal`, `welmsg`, `autochannels`, `blacklist`,`lvlmsg`, `whitelist`) VALUES (?, '0', 'rc!', 'Welcome %user% on %guild%', 'Bye %user%', '0', '0', 'closed', '0', '', '','1', '')");
            ps.setString(1, String.valueOf(guild.getIdLong()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String type) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM guilds WHERE serverid = ?");
            ps.setString(1, guild.getId());
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

    public boolean enabledWhitelist(){
        return !get("whitelist").equals("");
    }

    public boolean enabledBlacklist(){
        return !get("blacklist").equals("");
    }

    public boolean isBlacklisted(TextChannel channel){
        return get("blacklist").contains(channel.getId());
    }

    public boolean isWhitelisted(TextChannel channel){
        return get("whitelist").contains(channel.getId());
    }

}
