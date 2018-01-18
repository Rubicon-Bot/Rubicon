package fun.rubicon.core.webpanel;

import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public interface MessageStatisticsRequest {

    void setGuildCount(Guild guild, int size);
}
