/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.entities.impl;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.core.entities.cache.RubiconUserCache;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public abstract class RubiconUserImpl extends RethinkHelper {

    protected User user = null;
    private String userId = null;
    private String bio = null;
    private long money = 0;
    private long premium = 0;
    private String language = null;
    private String afk = null;
    private HashMap<String, List<String>> playlists = null;

    public Rethink rethink;
    private Table table;
    private Filter dbUser;

    private static RubiconUserCache userCache = new RubiconUserCache();

    public RubiconUserImpl(User user, String bio, long money, long premium, String language, String afk, HashMap<String, List<String>> playlists) {
        this.user = user;
        this.userId = user.getId();
        this.bio = bio;
        this.money = money;
        this.premium = premium;
        this.language = language;
        this.afk = afk;
        this.playlists = playlists;

        initRethink();
    }

    public RubiconUserImpl(RubiconUser rubiconUser) {
        this(rubiconUser.user, rubiconUser.getBio(), rubiconUser.getMoney(), rubiconUser.getPremiumRaw(), rubiconUser.getLanguage(), rubiconUser.getAFKState(), rubiconUser.getMusicPlaylists());
    }

    public RubiconUserImpl(User user, HashMap<String, ?> map) {
        this.user = user;
        if (map == null) {
            bio = "No bio set.";
            money = 0;
            premium = 0;
            language = "en-US";
            afk = null;
            playlists = null;
        }
        this.bio = map.containsKey("bio") ? (String) map.get("bio") : "No bio set.";
        this.money = map.containsKey("money") ? (Long) map.get("money") : 0;
        this.premium = map.containsKey("premium") ? (Long) map.get("premium") : 0;
        this.language = map.containsKey("language") ? (String) map.get("language") : "en-US";
        this.afk = map.containsKey("afk") ? (String) map.get("afk") : null;
        this.playlists = map.containsKey("playlists") ? (HashMap<String, List<String>>) map.get("playlists") : new HashMap<>();
        initRethink();
    }

    private void initRethink() {
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("users");
        dbUser = table.filter(rethink.rethinkDB.hashMap("userId", userId));
    }

    public void setBio(String bio) {
        this.bio = bio;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("bio", bio)).run(rethink.getConnection());
    }

    public String getBio() {
        return bio;
    }

    public void setMoney(long amount) {
        this.money = amount;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("money", amount)).run(rethink.getConnection());
    }

    public long getMoney() {
        return money;
    }

    public void addMoney(long amount) {
        setMoney(getMoney() + amount);
    }

    public void removeMoney(long amount) {
        setMoney(getMoney() - amount);
    }

    public void setPremium(long time) {
        this.premium = time;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("premium", time)).run(rethink.getConnection());
    }

    public long getPremiumRaw() {
        return premium;
    }

    public boolean isPremium() {
        if (getPremiumRaw() > new Date().getTime())
            return true;
        else
            setPremium(0);
        return false;
    }

    public void setLanguage(String languageKey) {
        this.language = languageKey;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("language", languageKey)).run(rethink.getConnection());
    }

    public String getLanguage() {
        return language;
    }

    private void update() {
        userCache.update(userId, new RubiconUser(user, bio, money, premium, language, afk, playlists));
    }

    public void setAFKState(String afk) {
        this.afk = afk;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("afk", afk)).run(rethink.getConnection());
    }

    public String getAFKState() {
        return afk;
    }

    public boolean isAFK() {
        try {
            if (getAFKState() == null)
                return false;
            return !getAFKState().equals("");
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Date getPremiumExpiryDate() {
        if (!this.isPremium())
            return null;
        return new Date(this.getPremiumRaw());
    }

    public String formatExpiryDate() {
        if (!this.isPremium())
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(TranslationUtil.translate(user, "date.format"));
        return sdf.format(this.getPremiumExpiryDate());
    }

    public void unban(Guild guild) {
        rethink.db.table("punishments").filter(rethink.rethinkDB.hashMap("userId", user.getId()).with("guildId", guild.getId()).with("type", "ban")).delete().run(rethink.getConnection());

        if (guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            guild.getController().unban(user).queue();
        } else
            guild.getOwner().getUser().openPrivateChannel().complete().sendMessage("ERROR: Unable to unban user `" + user.getName() + "`! Please give Rubicon `BAN_MEMBERS` permission in order to use the unban command").queue();
    }

    public void saveMusicPlaylist(List<String> links, String name) {
        HashMap<String, List<String>> pl = getMusicPlaylists();
        if (pl == null)
            pl = new HashMap<>();
        pl.put(name, links);
        saveMusicPlaylist(pl);
    }

    public void saveMusicPlaylist(HashMap<String, List<String>> list) {
        if (list == null)
            return;
        playlists = list;
        update();
        dbUser.update(rethink.rethinkDB.hashMap("playlists", null)).run(rethink.getConnection());
        dbUser.update(rethink.rethinkDB.hashMap("playlists", list)).run(rethink.getConnection());
    }

    public HashMap<String, List<String>> getMusicPlaylists() {
        return playlists;
    }

    public void delete() {
        dbUser.delete().run(rethink.getConnection());
        userCache.remove(userId);
    }

    private static RubiconUser create(User user) {
        RubiconBot.getRethink().db.table("users").insert(RubiconBot.getRethink().rethinkDB.array(RubiconBot.getRethink().rethinkDB.hashMap("userId", user.getId()))).run(RubiconBot.getRethink().getConnection());
        return new RubiconUser(user, "No bio set.", 0, 0, "en-US", null, null);
    }

    public User getUser() {
        return user;
    }

    public static RubiconUser fromUser(User user) {
        RubiconUser rubiconUser = userCache.getUser(user);
        if (rubiconUser == null)
            return create(user);
        return rubiconUser;
    }

    public static RubiconUserCache getUserCache() {
        return userCache;
    }
}