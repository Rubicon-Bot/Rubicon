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
import net.dv8tion.jda.core.entities.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a verifyusers table-row.
 */
public class VerificationUserSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final Member member;

    /**
     * Initializes this database entity.
     * @param mySQL the database.
     * @param member the member.
     */
    public VerificationUserSQL(MySQL mySQL, Member member) {
        this.mySQL = mySQL;
        this.member = member;
    }

    /**
     * @deprecated Use {@link #VerificationUserSQL(MySQL, Member)} instead.
     */
    @Deprecated
    public VerificationUserSQL(Guild guild, Member member) {
        this(RubiconBot.getMySQL(), member);
    }

    /**
     * Convenience method.
     * @return the guild of the member.
     * @see #getMember()
     */
    public Guild getGuild() {
        return member.getGuild();
    }

    /**
     * @return the member object.
     */
    public Member getMember() {
        return member;
    }

    /**
     * @return the verification message object.
     */
    public Message getMessage() {
        return member.getGuild().getTextChannelById(mySQL.getVerificationValue(member.getGuild(), "channelid"))
                .getMessageById(Long.parseLong(get("messageid"))).complete();
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifyusers` WHERE `userid` = ?;"); //TODO missing guildid check?
            selectStatement.setString(1, member.getUser().getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in verifyusers entry for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `verifyusers` SET " + type + " = ? WHERE `userid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, member.getUser().getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in verifyusers entry for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifyusers` WHERE `userid` = ?;");
            selectStatement.setString(1, member.getUser().getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking verifyusers entry existence for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        try {
            PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                    "INSERT INTO `verifyusers` (`guildid`, `userid`, `messageid`) VALUES (?,?,?);");
            insertStatement.setLong(1, member.getGuild().getIdLong());
            insertStatement.setLong(2, member.getUser().getIdLong());
            insertStatement.setLong(3, getMessage().getIdLong());
        } catch (SQLException e) {
            Logger.error("SQLException while creating verifyusers entry for member "
                    + member.getUser().getId() + " in guild " + member.getGuild().getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * @deprecated Use {@link #create()} instead.
     */
    @Deprecated
    public boolean insert() {
        try {
            create();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `verifyusers`" +
                        "(`id` INT NOT NULL AUTO_INCREMENT, " +
                        "`guildid` TEXT NOT NULL, " +
                        "`userid` TEXT NOT NULL, " +
                        "`messageid` TEXT NOT NULL," +
                        "PRIMARY KEY (`id`)) ENGINE = InnoDB;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static VerificationUserSQL generatorInstance(MySQL mySQL) {
        return new VerificationUserSQL(mySQL, null);
    }
}
