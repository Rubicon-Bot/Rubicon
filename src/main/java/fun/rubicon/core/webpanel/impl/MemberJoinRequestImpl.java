package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequest;
import fun.rubicon.core.webpanel.MemberJoinRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberJoinRequestImpl implements MemberJoinRequest, WebpanelRequest {

    private Guild guild;

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public HttpRequest build() {
        HttpRequest request = new HttpRequest(WebpanelData.BASE_URL);
        request.addParameter("type", WebpanelData.MEMBER_JOIN.getKey());
        request.addParameter("guildid", guild.getId());
        request.addParameter("guildname", guild.getName());
        return request;
    }
}
