/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.sql;

import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.font.TextHitInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a verifications table-row.
 * TODO database generator
 */
public class VerificationSQL implements DatabaseEntry {
    private final MySQL database;
    private final Guild guild;

    /**
     * Initializes this database entity.
     * @param database the database.
     * @param guild the guild.
     */
    public VerificationSQL(MySQL database, Guild guild) {
        this.database = database;
        this.guild = guild;
    }

    /**
     * Checks whether verification is enabled on this guild.
     * @return whether verification is enabled on this guild.
     */
    public boolean isEnabled() {
        return exists();
    }

    /**
     * Creates a verification entry for the target and enables verification by thus.
     * @param settings verification settings data container.
     */
    public void create(CommandVerification.VerificationSettings settings) {
        String kickText = settings.kicktext == null ? "0" : settings.kicktext;
        String emote = settings.emote == null
                ? "white_check_mark"
                : (settings.emote.getId() == null ? settings.emote.getName() : settings.emote.getId());
        try {
            PreparedStatement insertStatement = database.getActiveConnection().prepareStatement(
                    "INSERT INTO `verifications` (`guildid`, `channelid`, `roleid`, `text`, `verifiedtext`, " +
                            "`kicktime`, `kicktext`, `emote`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
            insertStatement.setString(1, guild.getId());
            insertStatement.setString(2, settings.channel.getId());
            insertStatement.setString(3, settings.role.getId());
            insertStatement.setString(4, settings.verifytext);
            insertStatement.setString(5, settings.verifiedtext);
            insertStatement.setString(6, String.valueOf(settings.kicktime));
            insertStatement.setString(7, kickText);
            insertStatement.setString(8, emote);
            insertStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while creating verifications entry for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Deletes this entry and disables verification on the guild by thus.
     */
    public void delete() {
        try {
            PreparedStatement deleteStatement = database.getActiveConnection().prepareStatement(
                    "DELETE FROM `verifications` WHERE `guildid` = ?;");
            deleteStatement.setString(1, guild.getId());
            deleteStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while deleting a verifications entry for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifications` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            ResultSet selectResult = selectStatement.executeQuery();
            return selectResult.next() ? selectResult.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in verifications entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = database.getActiveConnection().prepareStatement(
                    "UPDATE `verifications` SET " + type + " = ? WHERE `guildid` = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, guild.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in verifications entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Checks whether a verifications entry exists for the guild.
     * @return whether an entry exists for this guild.
     */
    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = database.getActiveConnection().prepareStatement(
                    "SELECT * FROM `verifications` WHERE `guildid` = ?;");
            selectStatement.setString(1, guild.getId());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking verifications entry existence for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    /**
     * Creates a verification entry for the target guild with default values. Should not be used as it might break things,
     * use {@link #create(CommandVerification.VerificationSettings)} instead to provide values right away.
     */
    @Override
    public void create() {
        create(new CommandVerification.VerificationSettings(
                guild.getDefaultChannel(),
                "Hi %user%, welcome on " + guild.getName() + ". Please confirm that you accept our rules by " +
                        "reacting with '?' within the next ten minutes. You will be kicked otherwise.",
                "Thank you %user% for accepting our rules. Have fun!",
                guild.getPublicRole(),
                10,
                "You have not accepted our rules. You can accept the rules at any time, just rejoin the server.",
                null
        ));
    }

    /**
     * @return the id of the {@link net.dv8tion.jda.core.entities.TextChannel} used for verification.
     */
    public String getChannelId() {
        return get("channelid");
    }
}
