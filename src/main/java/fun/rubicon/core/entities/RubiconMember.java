/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.impl.RubiconUserImpl;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.listener.events.PunishmentEvent;
import fun.rubicon.listener.events.UnpunishEvent;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.*;

/**
 * @author Yannick Seeger / ForYaSee, Michael Rittmeister / Schlaubi
 */
public class RubiconMember extends RubiconUserImpl {

    private Member member;
    private Guild guild;
    private Rethink rethink;
    private final Filter dbMember;

    public RubiconMember(Member member) {
        super(RubiconUser.fromUser(member.getUser()));
        this.member = member;
        this.guild = member.getGuild();
        this.rethink = RubiconBot.getRethink();
        dbMember = rethink.db.table("members").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()));
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
                rethink.rethinkDB.hashMap("guildId", guild.getId())
                        .with("type", "mute")
                        .with("userId", user.getId())
                        .with("expiry", 1L)
        )).run(rethink.getConnection());
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, 0L);
        RubiconBot.getEventManager().handle(new PunishmentEvent(guild.getJDA(), 200, guild, member, PunishmentType.MUTE, 0L));
        return this;
    }


    public RubiconMember mute(Date date) {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getId())
                        .with("userId", user.getId())
                        .with("type", "mute")
                        .with("expiry", date.getTime())
        )).run(rethink.getConnection());
        Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
        guild.getController().addSingleRoleToMember(member, muted).queue();
        RubiconBot.getPunishmentManager().getMuteCache().put(member, date.getTime());
        RubiconBot.getEventManager().handle(new PunishmentEvent(guild.getJDA(), 200, guild, member, PunishmentType.MUTE, date.getTime()));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                RubiconMember.fromMember(member).unmute(true);
            }
        }, date);
        return this;
    }


    public RubiconMember unmute(boolean removeRole) {
        rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()).with("type", "mute")).delete().run(rethink.getConnection());
        if (removeRole) {
            Role muted = RubiconGuild.fromGuild(guild).getMutedRole();
            guild.getController().removeSingleRoleFromMember(member, muted).queue();
        }
        RubiconBot.getPunishmentManager().getMuteCache().remove(member);
        RubiconBot.getEventManager().handle(new UnpunishEvent(guild.getJDA(), 200, guild, member, PunishmentType.MUTE));
        return this;
    }

    public boolean isMuted() {
        long res = getLong(rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()).with("type", "mute")).run(rethink.getConnection()), "expiry");
        if (res == 1L) return true;
        else return new Date(res).after(new Date());
    }

    public RubiconMember ban(Date expiry) {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getId())
                        .with("userId", user.getId())
                        .with("type", "ban")
                        .with("expiry", expiry.getTime())
        )).run(rethink.getConnection());
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
        RubiconBot.getEventManager().handle(new PunishmentEvent(guild.getJDA(), 200, guild, member, PunishmentType.BAN, expiry.getTime()));
        return this;
    }

    public RubiconMember ban() {
        rethink.db.table("punishments").insert(rethink.rethinkDB.array(
                rethink.rethinkDB.hashMap("guildId", guild.getId())
                        .with("userId", user.getId())
                        .with("type", "mute")
                        .with("expiry", 1L)
        )).run(rethink.getConnection());
        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().ban(user, 7).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to ban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use ban command").queue();
        RubiconBot.getEventManager().handle(new PunishmentEvent(guild.getJDA(), 200, guild, member, PunishmentType.BAN, 0L));
        return this;
    }

    public void delete() {
        dbMember.delete().run(rethink.getConnection());
    }

    private boolean exist() {
        return exist(retrieve());
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("members").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("userId", user.getId()))).run(rethink.getConnection());
    }

    private Cursor retrieve() {
        return dbMember.run(rethink.getConnection());
    }

    public void warn(String reason, Member moderator) {
        rethink.db.table("warns").insert(rethink.rethinkDB.hashMap("guildId", guild.getId()).with("userId", user.getId()).with("reason", reason).with("moderator", moderator.getUser().getId()).with("issueTime", String.valueOf(new Date().getTime()))).run(rethink.getConnection());
        punish();
    }

    public void unwarn(String id) {
        unpunish();
        rethink.db.table("warns").filter(rethink.rethinkDB.hashMap("id", id)).delete().run(rethink.getConnection());
    }

    public List<RubiconWarn> getWarns() {
        List<RubiconWarn> warnList = new ArrayList<>();
        Cursor cursor = rethink.db.table("warns").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId())).run(rethink.getConnection());
        for (Object obj : cursor) {
            Map map = (Map) obj;
            warnList.add(new RubiconWarn((String) map.get("id"), guild.getMemberById((String) map.get("userId")), (String) map.get("reason"), guild.getMemberById((String) map.get("moderator")), new Date(Long.parseLong((String) map.get("issueTime")))));
        }
        return warnList;
    }

    public boolean hasWarn(String id) {
        Cursor cursor = rethink.db.table("warns").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()).with("id", id)).run(rethink.getConnection());
        return !cursor.toList().isEmpty();
    }

    private void punish() {
        if (!punishmentExists(getWarnCount())) return;
        Cursor cursor = rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap().with("guildId", guild.getId()).with("amount", String.valueOf(getWarnCount()))).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        PunishmentType type = PunishmentType.valueOf((String) map.get("type"));
        long expiry = (long) map.get("length");
        switch (type) {
            case KICK:
                if (!guild.getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
                    guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("PUNISHMENT ERROR: Could not kick " + member.getAsMention() + " due to permission error").queue();
                    return;
                }
                guild.getController().kick(member).reason("Too many warns").queue();
                break;
            case BAN:
                if (expiry == 0L)
                    ban();
                else
                    ban(StringUtil.parseDate(expiry + "m"));
                break;
            case MUTE:
                if (expiry == 0L)
                    mute();
                else
                    mute(StringUtil.parseDate(expiry + "m"));
                break;
        }
        try {
            user.openPrivateChannel().complete().sendMessage(((String) map.get("message")).replace("%guild%", guild.getName()).replace("%warns%", String.valueOf(getWarnCount()))).queue();
        } catch (Exception ignored) {
        }
    }

    public void clearWarns() {
        rethink.db.table("warns").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId())).delete().run(rethink.getConnection());
    }

    private void unpunish() {
        if (!punishmentExists(getWarnCount())) return;
        Cursor cursor = rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap().with("guildId", guild.getId()).with("amount", String.valueOf(getWarnCount()))).run(rethink.getConnection());
        Map map = (Map) cursor.toList().get(0);
        PunishmentType type = PunishmentType.valueOf((String) map.get("type"));
        switch (type) {
            case BAN:
                RubiconUser.fromUser(user).unban(guild);
                break;
            case MUTE:
                unmute(true);
                break;
        }

    }

    private boolean punishmentExists(int warnCount) {
        Cursor cursor = rethink.db.table("warn_punishments").filter(rethink.rethinkDB.hashMap().with("guildId", guild.getId()).with("amount", String.valueOf(warnCount))).run(rethink.getConnection());
        return !cursor.toList().isEmpty();
    }

    public int getWarnCount() {
        return getWarns().size();
    }

    public static RubiconMember fromMember(Member member) {
        return new RubiconMember(member);
    }

    public boolean hasWarns() {
        return !getWarns().isEmpty();
    }

    public String translate(String key) {
        return TranslationUtil.translate(user, key);
    }
}
