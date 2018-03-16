/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.RubiconBot;
import fun.rubicon.mysql.MySQL;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public abstract class RubiconUserImpl {

    protected User user;
    protected MySQL mySQL;

    public RubiconUserImpl(User user) {
        this.user = user;
        this.mySQL = RubiconBot.getMySQL();

        createIfNotExist();
    }

    public User getUser() {
        return user;
    }

    public RubiconUserImpl setBio(String bio) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE users SET bio=? WHERE userid=?");
            ps.setString(1, bio);
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public String getBio() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT bio FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("bio") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public RubiconUserImpl setMoney(int amount) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE users SET money=? WHERE userid=?");
            ps.setInt(1, amount);
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public int getMoney() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT money FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("money") : 0;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return 0;
    }

    public RubiconUserImpl addMoney(int amount) {
        setMoney(getMoney() + amount);
        return this;
    }

    public RubiconUserImpl removeMoney(int amount) {
        setMoney(getMoney() - amount);
        return this;
    }

    public RubiconUserImpl setPremium(long time) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE users SET premium=? WHERE userid=?");
            ps.setLong(1, time);
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public long getPremiumRaw() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT premium FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getLong("premium") : 0;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return 0;
    }

    public boolean isPremium() {
        if (getPremiumRaw() == 0)
            return false;
        return true;
    }

    public RubiconUserImpl setLanguage(String languageKey) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE users SET language=? WHERE userid=?");
            ps.setString(1, languageKey);
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public String getLanguage() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT language FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("language") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }


    public RubiconUserImpl setAFKState(String afk) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE users SET afk=? WHERE userid=?");
            ps.setString(1, afk);
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public String getAFKState() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT afk FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("afk") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public boolean isAFK() {
        return !getAFKState().equals("none");
    }

    public void delete() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM users WHERE userid=?");
            ps.setLong(1, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private boolean exist() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM users WHERE userid = ?");
            ps.setLong(1, user.getIdLong());
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
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO users(`userid`, `bio`, `money`, `premium`, `language`, `afk`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setLong(1, user.getIdLong());
            ps.setString(2, "No bio set.");
            ps.setInt(3, 0);
            ps.setLong(4, 0);
            ps.setString(5, "en-US");
            ps.setString(6, "none");
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public RubiconUser unban(Guild guild) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM punishments WHERE serverid=? AND userid=? AND type='ban'");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().unban(user).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to ban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use ban command").queue();

        return ((RubiconUser) this);
    }

    public static RubiconUser fromUser(User user) {
        return new RubiconUser(user);
    }
}
