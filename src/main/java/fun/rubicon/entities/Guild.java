package fun.rubicon.entities;

import net.dv8tion.jda.core.entities.Role;

import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface Guild extends net.dv8tion.jda.core.entities.Guild {

    //Guild
    void deleteGuild();

    //Prefix
    String getPrefix();

    void setPrefix(String prefix);

    //Joinmessages
    Joinmessage getJoinmessage();

    void setJoinmessage(Joinmessage joinmessage);

    void enableJoinmessages(String channelId, String message);

    void disableJoinmessages();

    boolean hasJoinmessagesEnabled();

    // Leavemessages
    Leavemessage getLeavemessage();

    void setLeavemessage(Leavemessage leavemessage);

    void enableLeavemessages(String channelId, String message);

    void disableLeavemessages();

    boolean hasLeavemessageEnanled();

    // JoinImages
    Joinimage getJoinimage();

    void setJoinimage(Joinimage joinimage);

    void enableJoinimage(String channelId);

    void disableJoinimages();

    boolean hasJoinimagesEnabled();

    //Beta
    boolean isBeta();

    void enableBeta();

    void disableBeta();

    //Ranks
    boolean usesRanks();

    boolean isRank(Role role);

    void allowRank(Role role);

    void disallowRank(Role role);

    void updateRanks(List<String> idList);

    void checkRanks();

    List<Role> getRanks();

    List<String> getRankIds();

    //Portal

    // TODO: 5/22/18 Add Autochannels i am too dumb, LG Lee

}
