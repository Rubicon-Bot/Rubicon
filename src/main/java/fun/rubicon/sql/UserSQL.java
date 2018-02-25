/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

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
 * Represents a music_users table-row.
 * @author Yannick Seeger / ForYaSee
 */
public class UserSQL implements DatabaseGenerator, DatabaseEntry {
    private static final SimpleDateFormat PREMIUM_EXPIRY_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private final MySQL mySQL;
    private final User user;

    /**
     * Initializes this database entity and creates it if it does not exist.
     * @param mySQL the database.
     * @param user the user.
     */
    public UserSQL(MySQL mySQL, User user) {
        this.mySQL = mySQL;
        this.user = user;

        if (user != null)
            create();
    }

    /**
     * @deprecated Use {@link #UserSQL(MySQL, User)} instead.
     */
    @Deprecated
    public UserSQL(User user) {
        this(RubiconBot.getMySQL(), user);
    }

    /**
     * @deprecated Use {@link #UserSQL(MySQL, User)} instead.
     */
    @Deprecated
    public static UserSQL fromUser(User user) {
        return new UserSQL(RubiconBot.getMySQL(), user);
    }

    /**
     * @deprecated Use {@link #UserSQL(MySQL, User)} instead.
     */
    @Deprecated
    public static UserSQL fromMember(Member member) {
        return fromUser(member.getUser());
    }

    /**
     * @return whether the user currently has premium access.
     * @see #getPremiumExpiryDate() for the expiration date.
     */
    public boolean isPremium() {
        return getPremiumExpiryDate() != null;
    }

    /**
     * @return the premium expiry date for the user or null if the user does not have premium access.
     */
    public Date getPremiumExpiryDate() {
        String expiryDateEntry = get("premium");
        if(expiryDateEntry.equalsIgnoreCase("false"))
            return null;
        Date parsedExpiryDate = new Date(Long.parseLong(expiryDateEntry));
        if(parsedExpiryDate.before(new Date())) {
            set("premium", "false");
            return null;
        }
        return parsedExpiryDate;
    }

    /**
     * @return the formatted expiry date or null if the user currently has no premium access.
     */
    public String formatExpiryDate() {
        Date expiryDate = getPremiumExpiryDate();
        return expiryDate == null ? null : PREMIUM_EXPIRY_DATE_FORMAT.format(expiryDate);
    }

    /**
     * @return this entry's user object.
     */
    public User getUser() {
        return user;
    }

    /**
     * @param guild the guild.
     * @return a member object for the specified guild.
     */
    public Member getMember(Guild guild) {
        return guild.getMember(user);
    }

    /**
     * @param guild the guild.
     * @return the members sql entry for the user in the specified guild.
     * @see #getMember(Guild)
     */
    public MemberSQL getMemberSQL(Guild guild) {
        return new MemberSQL(mySQL, getMember(guild));
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `users` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in users entry for user "
                    + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            create();
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `users` SET " + type + " = ? WHERE `userid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, user.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in users entry for user "
                    + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `users` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking users entry existence for user " + user.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        if (!exists()) {
            try {
                PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                        "INSERT INTO `users` (`id`, `userid`, `bio`, `money`, `premium`) " +
                                "VALUES (0, ?, 'No bio set.', '1000', 'false')");
                insertStatement.setString(1, user.getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating users entry for user " + user.getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }
        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `users` (" +
                        "  `id` INT(250) NOT NULL AUTO_INCREMENT," +
                        "  `userid` VARCHAR(50) NOT NULL," +
                        "  `bio` TEXT NOT NULL," +
                        "  `money` VARCHAR(250)," +
                        "  `premium` VARCHAR(50) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static UserSQL generatorInstance(MySQL mySQL) {
        return new UserSQL(mySQL, null);
    }
}
