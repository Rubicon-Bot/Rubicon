package fun.rubicon.core.webpanel;

import de.foryasee.httprequest.HttpRequestBuilder;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class WebpanelManager extends ListenerAdapter implements Runnable {

    private final String requestToken;
    private final List<WebpanelRequest> requestList;

    private final Thread eventThread;
    private boolean running;

    private static Map<Guild, Integer> guildMessageCount = new HashMap<>();

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
        if (event.getAuthor().isBot())
            return;
        if (guildMessageCount.containsKey(event.getGuild())) {
            int old = guildMessageCount.get(event.getGuild());
            guildMessageCount.replace(event.getGuild(), old + 1);
        } else {
            guildMessageCount.put(event.getGuild(), 1);
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
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        for (WebpanelRequest request : requestList.stream().filter(request -> request instanceof GuildNameUpdateRequest).collect(Collectors.toList())) {
            ((GuildNameUpdateRequest) request).setGuild(event.getGuild());
            sendRequest(request.build());
        }
    }

    @Override
    public void run() {
        final long delay = 1000 * 60 * 30;
        long last = 0;
        long now;

        //Wait for JDA initialisation
        try {
            eventThread.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (running) {
            now = System.currentTimeMillis();
            if (last + delay <= now) {
                //Execute
                for (WebpanelRequest request : requestList) {
                    if (request instanceof MemberCountUpdateRequest) {
                        for (Guild guild : RubiconBot.getJDA().getGuilds()) {
                            ((MemberCountUpdateRequest) request).setGuild(guild);
                            sendRequest(request.build());
                        }
                    } else if (request instanceof MessageStatisticsRequest) {
                        for (Map.Entry entry : guildMessageCount.entrySet()) {
                            ((MessageStatisticsRequest) request).setGuildCount((Guild) entry.getKey(), (int) entry.getValue());
                            sendRequest(request.build());
                        }
                    }
                }

                //Reset Timer
                last = System.currentTimeMillis();
            }
        }
    }

    public void sendRequest(HttpRequestBuilder request) {
        try {
            request.addParameter("token", requestToken);
            RequestResponse response = request.sendRequest();
            if (response.getResponseCode() != 200) {
                Logger.debug(response.getResponseMessage());
                throw new Exception("Error while sending request to " + response.getEndpointUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
