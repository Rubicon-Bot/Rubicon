package fun.rubicon.features.portal;

import net.dv8tion.jda.core.entities.*;

import java.util.HashMap;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface Portal {

    Guild getRootGuild();

    TextChannel getRootChannel();

    HashMap<Guild, Channel> getMembers();

    void addGuild(String guildId, String channelId);

    void removeGuild(String guildId);

    void delete(String reason);

    void broadcast(String channelExclude, String message, String authorName, String avatarUrl, String guildName);

    void broadcastSystemMessage(MessageEmbed messageEmbed);

    void setPortalTopic(String topic);

    boolean containsChannel(Channel channel);
}
