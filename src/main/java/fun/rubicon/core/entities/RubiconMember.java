/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Yannick Seeger / ForYaSee, Michael Rittmeister / Schlaubi
 */
public class RubiconMember extends RubiconUserImpl {

    private Member member;
    private Guild guild;
    private Rethink rethink;
    private final Filter dbMember;

    public RubiconMember(Member member) {
        super(member.getUser());
        this.member = member;
        this.guild = member.getGuild();
        this.rethink = RubiconBot.getRethink();
        dbMember = rethink.db.table("members").filter(rethink.rethinkDB.hashMap("userId", user.getIdLong()).with("guildId", guild.getIdLong()));
        createIfNotExist();
    }

    public Member getMember() {
        return member;
    }

    public void setLevel(int level) {
        dbMember.update(rethink.rethinkDB.hashMap("level", level));
    }

    public int getLevel() {
        return Long.valueOf(getLong(retrieve(), "level")).intValue();
    }

    public void setPoints(int points) {
        dbMember.update(rethink.rethinkDB.hashMap("points", points));
    }

    public int getPoints() {
        return Long.valueOf(getLong(retrieve(), "points")).intValue();
    }


    public RubiconMember mute() {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getIdLong())
                        .with("type", "mute")
                        .with("userId", user.getIdLong())
                        .with("expiry", 0L)
        )).run(rethink.connection);
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, 0L);
        return this;
    }


    public RubiconMember mute(Date date) {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getIdLong())
                        .with("userId", user.getIdLong())
                        .with("type", "mute")
                        .with("expiry", date.getTime())
        )).run(rethink.connection);
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


    public RubiconMember unmute(boolean removeRole) {
        rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getIdLong()).with("guildId", guild.getIdLong()).with("type", "mute")).delete().run(rethink.connection);
        if (removeRole) {
            Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
            guild.getController().removeSingleRoleFromMember(member, muted).queue();
        }
        RubiconBot.getPunishmentManager().getMuteCache().remove(member);
        return this;
    }

    public boolean isMuted() {
        long res = getLong(rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getIdLong()).with("guildId", guild.getIdLong()).with("type", "mute")).run(rethink.connection), "expiry");
        if (res == 0L) return true;
        else return new Date(res).after(new Date());
    }

    public RubiconMember ban(Date expiry) {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getIdLong())
                        .with("userId", user.getIdLong())
                        .with("type", "ban")
                        .with("expiry", expiry.getTime())
        )).run(rethink.connection);
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
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getIdLong())
                        .with("userId", user.getIdLong())
                        .with("type", "mute")
                        .with("expiry", 0L)
        )).run(rethink.connection);
        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().ban(user, 7).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to ban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use ban command").queue();

        return this;
    }

    public void delete() {
        dbMember.delete().run(rethink.connection);
    }

    private boolean exist() {
        return exist(retrieve());
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("members").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getIdLong()).with("userId", user.getIdLong()))).run(rethink.connection);
    }

    private Cursor retrieve() {
        return dbMember.run(rethink.connection);
    }

    public static RubiconMember fromMember(Member member) {
        return new RubiconMember(member);
    }
}
