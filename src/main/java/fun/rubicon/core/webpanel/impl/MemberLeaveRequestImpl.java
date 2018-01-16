package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequest;
import fun.rubicon.core.webpanel.MemberLeaveRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberLeaveRequestImpl implements MemberLeaveRequest, WebpanelRequest {

    private Guild guild;

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public HttpRequest build() {
        HttpRequest request = new HttpRequest(WebpanelData.MEMBER_LEFT.getUrl());
        request.addParameter("guild", guild.getId());
        return request;
    }
}
