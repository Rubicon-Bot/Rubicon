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

    boolean isAfk();

    boolean isPremium();

    HashMap<String, List<String>> getPlaylists();

    void addMoney(Long money);

    void removeMoney(Long money);

    void setBio(String bio);

    void setMoney(Long money);

    void setLanguage(String language);

    void setAfk(String afk);

    void disableAfk();

    void setPremium(Long premium);

    void disablePremium();

    void setPlaylists(HashMap<String, List<String>> playlists);

    void delete();
}
