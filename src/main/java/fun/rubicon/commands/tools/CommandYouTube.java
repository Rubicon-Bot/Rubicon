package fun.rubicon.commands.tools;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.message;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandYouTube extends CommandHandler {


    private static HashMap<Long, AnnounceHolder> announceMap = new HashMap<>();

    public CommandYouTube() {
        super(new String[]{"youtube","yt"}, CommandCategory.TOOLS, new PermissionRequirements("youtube", false, false), "Announce your newest YouTube Videos! **Delete it by reentering the Same Values again**", "<#channel> <YouTube Channel ID>");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);
        if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
            return EmbedUtil.message(EmbedUtil.noPremium());
        if (invocation.getMessage().getMentionedChannels().isEmpty())
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.yt.mention.title"), invocation.translate("command.yt.mention.description")));
        Cursor cursor = RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", invocation.getGuild().getId())).run(RubiconBot.getRethink().getConnection());
        List l = cursor.toList();
        if (l.size() > 1) {
            String cretor = invocation.getArgs()[1].replace(" ", "");
            for (Object item : l
                    ) {
                Map map = (Map) item;
                if (map.get("youcreator").toString().equalsIgnoreCase(cretor)) {
                    RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("youcreator", map.get("youcreator"))).delete().run(RubiconBot.getRethink().getConnection());
                    return SafeMessage.sendMessageBlocking(invocation.getTextChannel(), new EmbedBuilder().setColor(Color.RED).setDescription(invocation.translate("command.yt.delete") + map.get("youcreator").toString()).build());
                }

            }

        }

        String creator = invocation.getArgs()[1].replace(" ", "");
        Request request = new Request.Builder()
                .url("https://youtube.com/channel/" + creator)
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() != 200) {
                response.close();
                return message(error(invocation.translate("command.yt.invalid.title"), invocation.translate("command.yt.invalid.description")));
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setTitle(invocation.translate("command.yt.setup.title")).setDescription(invocation.translate("command.yt.setup.description")).setFooter(invocation.translate("command.yt.setup.footer"), null)));
        announceMap.put(invocation.getAuthor().getIdLong(), new AnnounceHolder(invocation.getTextChannel(), invocation.getMessage().getMentionedChannels().get(0), creator, invocation.getAuthor(), infoMessage));

        return null;
    }

    public static void handle(MessageReceivedEvent event) {
        if (!announceMap.containsKey(event.getAuthor().getIdLong()))
            return;
        AnnounceHolder holder = announceMap.get(event.getAuthor().getIdLong());
        if (!event.getTextChannel().getId().equals(holder.textChannel.getId()))
            return;
        if (event.getMessage().getContentDisplay().contains(holder.creator))
            return;
        String description = event.getMessage().getContentRaw();
        RubiconBot.getRethink().db.table("youtube").insert(RubiconBot.getRethink().rethinkDB.array(
                RubiconBot.getRethink().rethinkDB.hashMap("guildId", holder.textChannel.getGuild().getId())
                        .with("youmsg", description)
                        .with("youchannel", holder.channel.getId())
                        .with("youcreator", holder.creator)
                        .with("lastvideo", "0")
        )).run(RubiconBot.getRethink().getConnection());
        String[] strings = getUrlTitle(holder.creator);
        if (!strings[0].equals("") && !strings[1].equals("")) {
            SafeMessage.sendMessage(holder.channel, description.replace("%url%", strings[0]).replace("%title%", strings[1]));
            RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", holder.textChannel.getGuild().getId())).update(RubiconBot.getRethink().rethinkDB.hashMap("lastvideo", strings[0])).run(RubiconBot.getRethink().getConnection());
        }
        holder.delete();
        event.getMessage().delete().queue();
    }

    private static String[] getUrlTitle(String channelid) {
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=CHANNEL_ID&maxResults=1&order=date&type=video&key=YOUR_API_KEY".replace("CHANNEL_ID", channelid).replace("YOUR_API_KEY", RubiconBot.getConfiguration().getString("google_token")))
                .build();
        String uri = "";
        String title = "";
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (!response.body().toString().isEmpty()) {
                JSONObject object = new JSONObject(response.body().string());
                JSONArray items = (JSONArray) object.get("items");
                if (!items.isNull(0)) {
                    JSONObject snippet = (JSONObject) ((JSONObject) items.get(0)).get("snippet");
                    JSONObject id = (JSONObject) ((JSONObject) items.get(0)).get("id");
                    title = (String) snippet.get("title");
                    uri = "http://youtu.be/" + id.get("videoId");
                } else {
                    uri = "https://youtu.be";
                    title = "No Video Found";
                }
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{uri, title};
    }


    private static class AnnounceHolder {
        private TextChannel textChannel;
        private TextChannel channel;
        private User author;
        private Message infoMessage;
        private String creator;
        private Timer timer;

        private AnnounceHolder(TextChannel textChannel, TextChannel channel, String creator, User author, Message infoMessage) {
            this.textChannel = textChannel;
            this.channel = channel;
            this.author = author;
            this.infoMessage = infoMessage;
            this.creator = creator;

            //Abort
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    infoMessage.delete().queue();
                    SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.error(TranslationUtil.translate(author, "command.yt.abort.title"), TranslationUtil.translate(author, "command.yt.abort.description"))));
                    announceMap.remove(author.getIdLong());
                }
            }, 60000);
        }

        private void delete() {
            announceMap.remove(author.getIdLong());
            timer.cancel();
            SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.success(TranslationUtil.translate(author, "command.yt.success.title"), TranslationUtil.translate(author, "command.yt.success.description"))));
            infoMessage.delete().queue();
        }
    }

    public static class YouTubeChecker {
        private List<Guild> guildList;
        private Rethink rethink;

        long delay = 300000; // delay in milliseconds 300000
        loadYouTube task = new loadYouTube();
        Timer timer = new Timer("YouTube-Loader");

        public YouTubeChecker(List<Guild> guilds) {
            this.guildList = guilds;
            this.rethink = RubiconBot.getRethink();

            start();

        }

        public void start() {
            timer.cancel();
            timer = new Timer("YouTube-Loader");
            Date executionDate = new Date(); // no params = now
            timer.scheduleAtFixedRate(task, executionDate, delay);
        }

        private class loadYouTube extends TimerTask {


            @Override
            public void run() {
                for (Guild guild :
                        guildList) {
                    Cursor cursor = rethink.db.table("youtube").filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
                    List l = cursor.toList();
                    if (l.size() < 1) {
                        continue;
                    }
                    for (Object item : l) {
                        Map map = (Map) item;
                        String creator = (String) map.get("youcreator");
                        String oldURI = (String) map.get("lastvideo");
                        String[] strings = getUrlTitle(creator);
                        if (!strings[0].equals(oldURI)) {
                            SafeMessage.sendMessage(guild.getTextChannelById(map.get("youchannel").toString()), map.get("youmsg").toString().replace("%url%", strings[0]).replace("%title%", strings[1]));
                            RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("youcreator", map.get("youcreator"))).update(RubiconBot.getRethink().rethinkDB.hashMap("lastvideo", strings[0])).run(RubiconBot.getRethink().getConnection());
                        }
                    }
                }
            }
        }


    }
}