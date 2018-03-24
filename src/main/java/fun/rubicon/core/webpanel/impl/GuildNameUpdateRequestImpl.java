package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.core.webpanel.GuildNameUpdateRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class GuildNameUpdateRequestImpl implements GuildNameUpdateRequest, WebpanelRequest {

    private Guild guild;

    @Override
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    @Override
    public HttpRequestBuilder build() {
        HttpRequestBuilder request = new HttpRequestBuilder(WebpanelData.BASE_URL, RequestType.GET);
        request.addParameter("type", "guild_name_update");
        request.addParameter("guildid", guild.getId());
        request.addParameter("guildname", guild.getName());
        return request;
    }
}
