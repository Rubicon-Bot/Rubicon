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

/**
 * Represents a members table-row.
 * @author Yannick Seeger / ForYaSee
 */
public class MemberSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final Member member;

    /**
     * Initializes this database entity.
     * @param mySQL the database.
     * @param member the member.
     */
    public MemberSQL(MySQL mySQL, Member member) {
        this.mySQL = mySQL;
        this.member = member;
    }

    /**
     * @deprecated Use {@link #MemberSQL(MySQL, Member)} instead.
     */
    @Deprecated
    public MemberSQL(Member member) {
        this.member = member;
        this.mySQL = RubiconBot.getMySQL();
        create();
    }

    /**
     * @deprecated Use {@link #MemberSQL(MySQL, Member)} instead.
     */
    @Deprecated
    public static MemberSQL fromUser(User user, Guild guild) {
        return new MemberSQL(RubiconBot.getMySQL(), guild.getMember(user));
    }

    /**
     * @return the {@link UserSQL} for this {@link MemberSQL}.
     */
    public UserSQL getUserSQL() {
        return UserSQL.fromUser(member.getUser());
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `members` WHERE `userid` = ? AND `serverid` = ?;");
            selectStatement.setString(1, member.getUser().getId());
            selectStatement.setString(2, member.getGuild().getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in members entry for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            create();
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `members` SET " + type + " = ? WHERE `userid` = ? AND serverid = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, member.getUser().getId());
            updateStatement.setString(3, member.getGuild().getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in members entry for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `members` WHERE `userid` = ? AND `serverid` = ?;");
            selectStatement.setString(1, member.getUser().getId());
            selectStatement.setString(2, member.getGuild().getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking members entry existence for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        if (!exists()) {
            try {
                PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                        "INSERT INTO members(`id`, `userid`, `serverid`, `permissionlevel`, `level`, `points`) " +
                                "VALUES (0, ?, ?, '', '0', '0');");
                insertStatement.setString(1, member.getUser().getId());
                insertStatement.setString(2, member.getGuild().getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating guilds entry for member "
                        + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }

        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `members` (" +
                        "  `id` INT(250) NOT NULL AUTO_INCREMENT," +
                        "  `userid` VARCHAR(50) NOT NULL," +
                        "  `serverid` VARCHAR(50) NOT NULL," +
                        "  `permissionlevel` VARCHAR(50) NOT NULL," +
                        "  `level` VARCHAR(50) NOT NULL," +
                        "  `points` VARCHAR(50) NOT NULL," +
                        "  PRIMARY KEY (`id`)" +
                        ") ENGINE=InnoDB AUTO_INCREMENT=3918 DEFAULT CHARSET=utf8;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static MemberSQL generatorInstance(MySQL mySQL) {
        return new MemberSQL(mySQL, null);
    }
}
