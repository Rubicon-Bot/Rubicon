/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.mysql.MySQL;
import fun.rubicon.util.Info;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconGuild {

    private Guild guild;
    private MySQL mySQL;

    public RubiconGuild(Guild guild) {
        this.guild = guild;
        this.mySQL = RubiconBot.getMySQL();

        createIfNotExist();
    }

    public Guild getGuild() {
        return guild;
    }

    public void setPrefix(String prefix) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE guilds SET prefix=? WHERE serverid=?");
            ps.setString(1, prefix);
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public String getPrefix() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT prefix FROM guilds WHERE serverid = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("prefix") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void delete() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM guilds WHERE serverid=?");
            ps.setLong(1, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private boolean exist() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM guilds WHERE serverid = ?");
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return false;
    }

    private void createIfNotExist() {
        if (exist())
            return;
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO guilds(`serverid`, `prefix`) VALUES (?, ?)");
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, Info.BOT_DEFAULT_PREFIX);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public static RubiconGuild fromGuild(Guild guild) {
        return new RubiconGuild(guild);
    }
}
