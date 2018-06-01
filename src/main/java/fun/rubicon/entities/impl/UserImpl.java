package fun.rubicon.entities.impl;

import fun.rubicon.entities.User;
import fun.rubicon.io.db.RethinkDataset;
import lombok.Getter;
import lombok.ToString;
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
@ToString
public class UserImpl extends RethinkDataset implements User {

    public static final transient String TABLE = "users";
    private transient net.dv8tion.jda.core.entities.User user;
    private String id;
    @Getter
    private String bio;
    @Getter
    private Long money;
    @Getter
    private String language;
    @Getter
    private String afk;
    @Getter
    private Long premium;
    @Getter
    private HashMap<String, List<String>> playlists;

    public UserImpl(net.dv8tion.jda.core.entities.User user, String bio, Long money, String language, String afk, Long premium, HashMap<String, List<String>> playlists) {
        super(TABLE);
        this.user = user;
        this.bio = bio;
        this.money = money;
        this.language = language;
        this.afk = afk;
        this.premium = premium;
        this.playlists = playlists;
        this.id = user.getId();
    }

    public UserImpl() {
        super(TABLE);
    }

    public void setJDAUser(net.dv8tion.jda.core.entities.User user) {
        this.user = user;
    }

    @Override
    public void setBio(String bio) {
        this.bio = bio;
        saveData();
    }

    @Override
    public void setMoney(Long money) {
        this.money = money;
        saveData();
    }

    @Override
    public void addMoney(Long money) {
        setMoney(getMoney() + money);
    }

    @Override
    public void removeMoney(Long money) {
        setMoney(getMoney() - money);
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
        saveData();
    }

    @Override
    public void setAfk(String afk) {
        this.afk = afk;
        saveData();
    }

    @Override
    public void disableAfk() {
        setAfk(null);
    }

    @Override
    public boolean isAfk() {
        return getAfk() != null;
    }

    @Override
    public void setPremium(Long premium) {
        this.premium = premium;
        saveData();
    }

    @Override
    public boolean isPremium() {
        Long time = getPremium();
        if (time <= new Date().getTime()) {
            setPremium(0L);
            return false;
        }
        return true;
    }

    @Override
    public void disablePremium() {
        setPremium(0L);
    }

    @Override
    public void setPlaylists(HashMap<String, List<String>> playlists) {
        this.playlists = playlists;
        saveData();
    }

    @Override
    public void deleteUser() {
        deleteData();
    }

    @Override
    public String getId() {
        return id;
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
