/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a verifykicks table-row.
 */
public class VerificationKickSQL implements DatabaseGenerator, DatabaseEntry {
    private final MySQL mySQL;
    private final Guild guild;
    private final User user;

    /**
     * Initializes this database entity.
     * @param mySQL the database.
     * @param user the user.
     */
    public VerificationKickSQL(MySQL mySQL, Guild guild, User user) {
        this.mySQL = mySQL;
        this.guild = guild;
        this.user = user;
    }

    /**
     * @deprecated Use {@link #VerificationKickSQL(MySQL, Guild, User)} instead.
     */
    @Deprecated
    public VerificationKickSQL(User user, Guild guild) {
        this(RubiconBot.getMySQL(), guild, user);
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifykicks` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in verifykicks entry for user "
                    + user.getId() + " in guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = mySQL.getActiveConnection().prepareStatement(
                    "UPDATE `verifykicks` SET " + type + " = ? WHERE `userid` = ?;"); //TODO does not check guild
            updateStatement.setString(1, value);
            updateStatement.setString(2, user.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in verifykicks entry for user "
                    + user.getId() + " in guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifykicks` WHERE `userid` = ?;");
            selectStatement.setString(1, user.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking verifykicks entry existence for user "
                    + user.getId() + " in guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        throw new NotImplementedException();
    }

    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " +
                "`verifykicks` " +
                "( `id` INT NOT NULL AUTO_INCREMENT ," +
                " `guildid` TEXT NOT NULL ," +
                " `userid` TEXT NOT NULL ," +
                " `kickText` TEXT NOT NULL ," +
                " `kickTime` TEXT NOT NULL," +
                " `message` TEXT NOT NULL, " +
                " PRIMARY KEY (`id`)) ENGINE = InnoDB;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static VerificationKickSQL generatorInstance(MySQL mySQL) {
        return new VerificationKickSQL(mySQL, null, null);
    }
}
