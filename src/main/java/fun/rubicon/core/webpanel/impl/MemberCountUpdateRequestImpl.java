package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequest;
import fun.rubicon.core.webpanel.MemberCountUpdateRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberCountUpdateRequestImpl implements MemberCountUpdateRequest, WebpanelRequest {

    private Guild guild;

    @Override
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public HttpRequest build() {
        HttpRequest request = new HttpRequest(WebpanelData.BASE_URL);
        request.addParameter("type", WebpanelData.MEMBER_COUNT_UPDATE.getKey());
        request.addParameter("guildid", guild.getId());
        request.addParameter("guildname", guild.getName());
        request.addParameter("count", String.valueOf(guild.getMembers().size()));
        return request;
    }
}
