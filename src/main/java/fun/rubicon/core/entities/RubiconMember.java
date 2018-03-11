/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Yannick Seeger / ForYaSee, Michael Rittmeister / Schlaubi
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

    public RubiconMember setLevel(int level) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE members SET level=? WHERE userid=? AND serverid=?");
            ps.setInt(1, level);
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
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

    public RubiconMember setPoints(int points) {
        try {
            PreparedStatement ps = mySQL.prepareStatement("UPDATE members SET points=? WHERE userid=? AND serverid=?");
            ps.setInt(1, points);
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, guild.getIdLong());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
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

    private RubiconMember createIfNotExist() {
        if (exist())
            return this;
        try {
            PreparedStatement ps = mySQL.prepareStatement("INSERT INTO members(`userid`, `serverid`, `level`, `points`, `mute`) VALUES (?, ?, ?, ?, '')");
            ps.setLong(1, user.getIdLong());
            ps.setLong(2, guild.getIdLong());
            ps.setInt(3, 0);
            ps.setLong(4, 0);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        return this;
    }

    public RubiconMember mute(){
<<<<<<< HEAD
=======
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("INSERT INTO punishments(type, serverid, userid, expiry) VALUES('mute',?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, 0L);
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, 0L);
        return this;
    }

    public RubiconMember mute(Date date){
>>>>>>> Rework-1.0.0
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("INSERT INTO punishments(type, serverid, userid, expiry) VALUES('mute',?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
<<<<<<< HEAD
            ps.setLong(3, 0L);
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, 0L);
        return this;
    }

    public RubiconMember mute(Date date){
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("INSERT INTO punishments(type, serverid, userid, expiry) VALUES('mute',?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, date.getTime());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, date.getTime());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               RubiconMember.fromMember(member).unmute(true);
            }
        }, date);
        return this;
    }

    public RubiconMember unmute(boolean removeRole){
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM punishments WHERE type='mute' AND serverid=? AND userid=?");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
=======
            ps.setLong(3, date.getTime());
>>>>>>> Rework-1.0.0
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
<<<<<<< HEAD
        if(removeRole) {
            Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
            guild.getController().removeSingleRoleFromMember(member, muted).queue();
        }
        RubiconBot.getPunishmentManager().getMuteCache().remove(member);
        return this;
    }

=======
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, date.getTime());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               RubiconMember.fromMember(member).unmute(true);
            }
        }, date);
        return this;
    }

    public RubiconMember unmute(boolean removeRole){
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM punishments WHERE type='mute' AND serverid=? AND userid=?");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.execute();
        } catch (SQLException e){
            Logger.error(e);
        }
        if(removeRole) {
            Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
            guild.getController().removeSingleRoleFromMember(member, muted).queue();
        }
        RubiconBot.getPunishmentManager().getMuteCache().remove(member);
        return this;
    }

>>>>>>> Rework-1.0.0
    public boolean isMuted(){
        try{
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT expiry FROM punishments WHERE serverid=? AND userid=? AND type='mute'");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                long expiry = rs.getLong("expiry");
                if(expiry == 0L) return true;
                else
                    if(new Date(expiry).after(new Date())) return true;
                else
                    return false;
            } else
                return false;
        } catch (SQLException e){
            Logger.error(e);
            return false;
        }
    }

<<<<<<< HEAD
    public RubiconMember ban(Date expiry) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("INSERT INTO punishments(type, serverid, userid, expiry) VALUES ('ban', ?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, expiry.getTime());
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);

        }
        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().ban(user, 7).queue();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    RubiconUser.fromUser(user).unban(guild);
                }
            }, expiry);
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to ban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use ban command").queue();
        return this;
    }

    public RubiconMember ban() {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("INSERT INTO punishments(type, serverid, userid, expiry) VALUES ('ban', ?,?,?)");
            ps.setLong(1, guild.getIdLong());
            ps.setLong(2, user.getIdLong());
            ps.setLong(3, 0L);
            ps.execute();
        } catch (SQLException e) {
            Logger.error(e);
        }
        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().ban(user, 7).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to ban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use ban command").queue();

        return this;
    }
=======



>>>>>>> Rework-1.0.0


    public static RubiconMember fromMember(Member member) {
        return new RubiconMember(member);
    }
}
