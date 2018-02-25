package fun.rubicon.sql;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a music-guilds table-row.
 * @author Yannick Seeger / ForYaSee
 */
public class GuildMusicSQL implements DatabaseGenerator, DatabaseEntry{
    private final MySQL mySQL;
    private final Guild guild;

    /**
     * Initializes this database entity and creates it if it does not exist.
     * @param mySQL the database.
     * @param guild the guild.
     */
    public GuildMusicSQL(MySQL mySQL, Guild guild) {
        this.mySQL = mySQL;
        this.guild = guild;

        if(guild != null)
            create();
    }

    /**
     * Initializes this database entity and creates it if it does not exist.
     * @param guild the guild
     * @deprecated Use {@link #GuildMusicSQL(MySQL, Guild)} instead.
     */
    @Deprecated
    public GuildMusicSQL(Guild guild) {
        this(RubiconBot.getMySQL(), guild);
    }

    @Override
    public String get(String type) {
        try {
            PreparedStatement getStatement = mySQL.getActiveConnection()
                    .prepareStatement("SELECT * FROM `music_guilds` WHERE `guildid` = ?;");
            getStatement.setString(1, guild.getId());
            ResultSet getResults = getStatement.executeQuery();
            return getResults.next() ? getResults.getString(type) : null;
        } catch (SQLException e) {
            Logger.error("SQLException while retrieving '" + type + "' value in music_guilds entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void set(String type, String value) {
        try {
            PreparedStatement updateStatement = mySQL.getActiveConnection()
                    .prepareStatement("UPDATE `music_guilds` SET " + type + " = ? WHERE guildid = ?;");
            updateStatement.setString(1, value);
            updateStatement.setString(2, guild.getId());
            updateStatement.execute();
        } catch (SQLException e) {
            Logger.error("SQLException while updating '" + type + "' in music_guilds entry for guild "
                    + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public boolean exists() {
        try {
            PreparedStatement selectStatement = mySQL.getActiveConnection().prepareStatement(
                    "SELECT * FROM `music_guilds` WHERE `guildid` = ?;");
            selectStatement.setLong(1, guild.getIdLong());
            return selectStatement.executeQuery().next();
        } catch (SQLException e) {
            Logger.error("SQLException while checking music_guilds entry existence for guild " + guild.getId() + ":");
            Logger.error(e);
            throw new RuntimeException("Something went wrong in our database.");
        }
    }

    @Override
    public void create() {
        if(!exists()) {
            try {
                PreparedStatement insertStatement = mySQL.getActiveConnection().prepareStatement(
                        "INSERT INTO music_guilds (guildid, dj, locked_channel) VALUES (?, 'false', 'false')");
                insertStatement.setString(1, guild.getId());
                insertStatement.execute();
            } catch (SQLException e) {
                Logger.error("SQLException while creating music_guilds entry for guild " + guild.getId() + ":");
                Logger.error(e);
                throw new RuntimeException("Something went wrong in our database.");
            }
        }
    }

    /**
     * Table creation script.
     * @throws SQLException if any sql error occurs.
     */
    @Override
    public void createTableIfNotExist() throws SQLException {
        mySQL.getActiveConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS `music_guilds` (\n" +
                " `id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                " `guildid` VARCHAR(50) NOT NULL,\n" +
                " `dj` VARCHAR(50) NOT NULL,\n" +
                " `locked_channel` VARCHAR(50) NOT NULL,\n" +
                " PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        ).execute();
    }

    /**
     * Creates an instance that should only be used for database creation.
     * @param mySQL the database.
     * @return an instance.
     */
    public static GuildMusicSQL generatorInstance(MySQL mySQL) {
        return new GuildMusicSQL(mySQL, null);
    }
}
