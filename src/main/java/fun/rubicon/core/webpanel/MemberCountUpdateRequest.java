package fun.rubicon.core.webpanel;

import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public interface MemberCountUpdateRequest {

    void setGuild(Guild guild);
}
