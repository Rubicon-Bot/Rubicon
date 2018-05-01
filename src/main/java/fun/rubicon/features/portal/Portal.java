package fun.rubicon.features.portal;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface Portal {

    Guild getRootGuild();

    TextChannel getRootChannel();

    HashMap<Guild, Channel> getMembers();

    void addGuild(String guildId, String channelId);

    void delete();
}
