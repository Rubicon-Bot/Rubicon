package fun.rubicon.entities;

import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface User extends net.dv8tion.jda.core.entities.User {

    String getBio();

    Long getMoney();

    String getLanguage();

    String getAfk();

    Long getPremium();

    HashMap<String, List<String>> getPlaylists();

    void setBio(String bio);

    void setMoney(Long money);

    void addMoney(Long money);

    void removeMoney(Long money);

    void setLanguage(String language);

    void setAfk(String afk);

    void disableAfk();

    void setPremium(Long premium);

    void disablePremium();

    void setPlaylists(HashMap<String, List<String>> playlists);

    boolean isAfk();

    boolean isPremium();

    void delete();
}
