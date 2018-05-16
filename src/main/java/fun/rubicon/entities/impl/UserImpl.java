package fun.rubicon.entities.impl;

import fun.rubicon.entities.User;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class UserImpl extends RethinkDataset implements User, net.dv8tion.jda.core.entities.User {

    public static final transient String TABLE = "users";
    private final transient net.dv8tion.jda.core.entities.User user;
    @Getter private String bio;
    @Getter private long money;
    @Getter private String language;
    @Getter private String afk;
    @Getter private long premium;
    @Getter private HashMap<String, List<String>> playlists;

    public UserImpl(net.dv8tion.jda.core.entities.User user, String bio, long money, String language, String afk, long premium,  HashMap<String, List<String>> playlists) {
        super(TABLE);
        this.user = user;
        this.bio = bio;
        this.money = money;
        this.language = language;
        this.afk = afk;
        this.premium = premium;
        this.playlists = playlists;
    }

    public void setBio(String bio) {
        this.bio = bio;
        save();
    }

    public void setMoney(long money) {
        this.money = money;
        save();
    }

    @Override
    public void addMoney(long money) {
        setMoney(getMoney() + money);
    }

    @Override
    public void removeMoney(long money) {
        setMoney(getMoney() - money);
    }

    public void setLanguage(String language) {
        this.language = language;
        save();
    }

    public void setAfk(String afk) {
        this.afk = afk;
        save();
    }

    @Override
    public void disableAfk() {
        setAfk(null);
    }

    @Override
    public boolean isAfk() {
        return getAfk() != null;
    }

    public void setPremium(long premium) {
        this.premium = premium;
        save();
    }

    @Override
    public boolean isPremium() {
        long time = getPremium();
        if(time <= new Date().getTime()) {
            setPremium(0);
            return false;
        }
        return true;
    }

    @Override
    public void disablePremium() {
        setPremium(0);
    }

    public void setPlaylists(HashMap<String, List<String>> playlists) {
        this.playlists = playlists;
        save();
    }

    @Override
    public String getId() {
        return user.getId();
    }

    //JDA User
    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getDiscriminator() {
        return user.getDiscriminator();
    }

    @Override
    public String getAvatarId() {
        return user.getAvatarId();
    }

    @Override
    public String getAvatarUrl() {
        return user.getAvatarUrl();
    }

    @Override
    public String getDefaultAvatarId() {
        return user.getDefaultAvatarId();
    }

    @Override
    public String getDefaultAvatarUrl() {
        return user.getDefaultAvatarUrl();
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return user.getEffectiveAvatarUrl();
    }

    @Override
    public boolean hasPrivateChannel() {
        return user.hasPrivateChannel();
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return user.openPrivateChannel();
    }

    @Override
    public List<Guild> getMutualGuilds() {
        return user.getMutualGuilds();
    }

    @Override
    public boolean isBot() {
        return user.isBot();
    }

    @Override
    public JDA getJDA() {
        return user.getJDA();
    }

    @Override
    public boolean isFake() {
        return user.isFake();
    }

    @Override
    public String getAsMention() {
        return user.getAsMention();
    }

    @Override
    public long getIdLong() {
        return user.getIdLong();
    }
}
