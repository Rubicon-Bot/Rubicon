package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class UserSQL implements DatabaseGenerator {

    private Connection connection;
    private MySQL mySQL;
    private User user;

    /**
     * Uses for database generation
     */
    public UserSQL() {
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();
    }

    /**
     * User fromUser(User user) or fromMember(Member member) method
     *
     * @see UserSQL
     */
    @Deprecated
    public UserSQL(User user) {
        this.user = user;
        this.mySQL = RubiconBot.getMySQL();
        this.connection = MySQL.getConnection();

        create();
    }

    private UserSQL(User user, MySQL mySQL, Connection connection) {
        this.user = user;
        this.mySQL = mySQL;
        this.connection = connection;
    }

    public static UserSQL fromUser(User user) {
        return new UserSQL(user, RubiconBot.getMySQL(), MySQL.getConnection());
    }

    public static UserSQL fromMember(Member member) {
        return fromUser(member.getUser());
    }


    //User Stuff
    public boolean exist() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE userid = ?");
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
            if (!exist())
                create();
            PreparedStatement ps = connection.prepareStatement("UPDATE users SET " + type + " = '" + value + "' WHERE userid = ?");
            ps.setString(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String get(String type) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE `userid` = ?");
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

    public void create() {
        if (exist())
            return;
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users(`id`, `userid`, `bio`, `money`, `premium`) VALUES (0, ?, ?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, "No bio set.");
            ps.setString(3, "1000");
            ps.setString(4, "false");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPremium() {
        String entry = get("premium");
        if (entry.equalsIgnoreCase("false")) {
            return false;
        }
        Date expiry = new Date(Long.parseLong(this.get("premium")));
        Date now = new Date();
        if (expiry.before(now)) {
            this.set("premium", "false");
            return false;
        }
        return true;
    }

    public Date getPremiumExpiryDate() {
        if (!this.isPremium())
            return null;
        return new Date(Long.parseLong(this.get("premium")));
    }

    public String formatExpiryDate() {
        if (!this.isPremium())
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(this.getPremiumExpiryDate());
    }

    public User getUser() {
        return RubiconBot.getJDA().getUserById(this.get("userid"));
    }

    public Member getMember(Guild guild) {
        return guild.getMember(this.getUser());
    }

    public MemberSQL getMemberSQL(Guild guild) {
        return MemberSQL.fromUser(this.user, guild);
    }

    @Override
    public void createTableIfNotExist() {
        try {
            if (connection.isClosed())
                mySQL.connect();
            PreparedStatement ps = connection.prepareStatement("" +
                    "CREATE TABLE IF NOT EXISTS `users` (" +
                    "  `id` INT(250) NOT NULL AUTO_INCREMENT," +
                    "  `userid` VARCHAR(50) NOT NULL," +
                    "  `bio` TEXT NOT NULL," +
                    "  `money` VARCHAR(250)," +
                    "  `premium` VARCHAR(50) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }


}
