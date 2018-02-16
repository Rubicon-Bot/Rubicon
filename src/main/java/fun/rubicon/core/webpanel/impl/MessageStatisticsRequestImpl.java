package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestType;
import fun.rubicon.core.webpanel.MessageStatisticsRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MessageStatisticsRequestImpl implements MessageStatisticsRequest, WebpanelRequest {

    private Guild guild;
    private int size;

    @Override
    public void setGuildCount(Guild guild, int size) {
        this.guild = guild;
        this.size = size;
    }

    @Override
    public HttpRequestBuilder build() {
        HttpRequestBuilder request = new HttpRequestBuilder(WebpanelData.BASE_URL, RequestType.GET);
        request.addParameter("type", WebpanelData.MESSAGE_COUNT.getKey());
        request.addParameter("guildid", guild.getId());
        request.addParameter("guildname", guild.getName());
        request.addParameter("count", String.valueOf(size));
        return request;
    }
}
