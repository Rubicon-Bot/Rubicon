/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubiconMember extends RubiconUserImpl {

    private Member member;
    private Guild guild;

    public RubiconMember(Member member) {
        super(member.getUser());

        this.member = member;
        this.guild = member.getGuild();

        createIfNotExist();
    }

    public Member getMember() {
        return member;
    }

    public void setLevel(int level) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE members SET level=? WHERE userid=? AND serverid=?");
            ps.setInt(1, level);
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public String getLevel() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT level FROM members WHERE userid=? AND serverid=?");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("level") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void setPoints(int points) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE members SET points=? WHERE userid=? AND serverid=?");
            ps.setInt(1, points);
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public String getPoints() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT points FROM members WHERE userid=? AND serverid=?");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("points") : null;
        } catch (SQLException e) {
            Logger.error(e);
        }
        return null;
    }

    public void delete() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("DELETE FROM members WHERE userid=? AND serverid=?");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    private boolean exist() {
        try {
            PreparedStatement ps = mySQL.prepareStatement("SELECT id FROM members WHERE userid=? AND serverid=?");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
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
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO members(`userid`, `serverid`, `level`, `points`) VALUES (?, ?, ?, ?)");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
            ps.setInt(3, 0);
            ps.setLong(4, 0);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    public static RubiconMember fromMember(Member member) {
        return new RubiconMember(member);
    }
}
