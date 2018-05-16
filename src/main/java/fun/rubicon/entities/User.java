package fun.rubicon.entities;

import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface User {

    String getBio();

    long getMoney();

    String getLanguage();

    String getAfk();

    long getPremium();

    List<String> getPlaylists();

    void setBio(String bio);

    void setMoney(long money);

    void addMoney(long money);

    void removeMoney(long money);

    void setLanguage(String language);

    void setAfk(String afk);

    void disableAfk();

    void setPremium(long premium);

    void disablePremium();

    void setPlaylists(HashMap<String, List<String>> playlists);

    boolean isAfk();

    boolean isPremium();
}
