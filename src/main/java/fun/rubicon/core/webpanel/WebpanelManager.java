package fun.rubicon.core.webpanel;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class WebpanelManager extends ListenerAdapter implements Runnable {

    private final String requestToken;
    private final List<WebpanelRequest> requestList;

    private final Thread eventThread;
    private boolean running;

    public WebpanelManager(String requestToken) {
        this.requestToken = requestToken;
        requestList = new ArrayList<>();

        eventThread = new Thread(this);
        running = true;
        eventThread.setName("WebpanelAPI");
        eventThread.start();
    }

    public void addRequest(WebpanelRequest request) {
        requestList.add(request);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (WebpanelRequest request : requestList.stream().filter(request -> request instanceof MessageStatisticsRequest).collect(Collectors.toList())) {
            ((MessageStatisticsRequest) request).setMessage(event.getMessage());
            sendRequest(request.build());
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        for (WebpanelRequest request : requestList.stream().filter(request -> request instanceof MemberJoinRequest).collect(Collectors.toList())) {
            ((MemberJoinRequest) request).setGuild(event.getGuild());
            sendRequest(request.build());
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        for (WebpanelRequest request : requestList.stream().filter(request -> request instanceof MemberLeaveRequest).collect(Collectors.toList())) {
            ((MemberLeaveRequest) request).setGuild(event.getGuild());
            sendRequest(request.build());
        }
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis() + 5000;
        long delay = 1000 * 60 * 60;
        long now;
        while (running) {
            now = System.currentTimeMillis();
            if (last + delay <= now) {
                Logger.debug("Test");
                //Execute
                for (WebpanelRequest request : requestList) {
                    if (request instanceof MemberCountUpdateRequest) {
                        for (Guild guild : RubiconBot.getJDA().getGuilds()) {
                            ((MemberCountUpdateRequest) request).setGuild(guild);
                            sendRequest(request.build());
                        }
                    }
                }

                //Reset Timer
                last = System.currentTimeMillis();
            }
        }
    }

    public void sendRequest(HttpRequest request) {
        try {
            request.addParameter("token", requestToken);
            RequestResponse response = request.sendGETRequest();
            if (response.getResponseCode() != 202) {
                throw new Exception("Error while sending request to " + request.getRequestURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
