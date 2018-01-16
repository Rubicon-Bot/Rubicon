package fun.rubicon.core.webpanel.impl;

import de.foryasee.httprequest.HttpRequest;
import fun.rubicon.core.webpanel.MessageStatisticsRequest;
import fun.rubicon.core.webpanel.WebpanelData;
import fun.rubicon.core.webpanel.WebpanelRequest;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MessageStatisticsRequestImpl implements MessageStatisticsRequest, WebpanelRequest {

    private Message message;

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public HttpRequest build() {
        HttpRequest request = new HttpRequest(WebpanelData.BASE_URL);
        request.addParameter("type", WebpanelData.MESSAGE_COUNT.getKey());
        request.addParameter("guildid", message.getGuild().getId());
        request.addParameter("guildname", message.getGuild().getName());
        return request;
    }
}
