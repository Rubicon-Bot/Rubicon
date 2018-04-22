package fun.rubicon.commands.general;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
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

import java.io.IOException;
import java.util.*;

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
        super(new String[]{"youtube"}, CommandCategory.GENERAL, new PermissionRequirements("youtube", false, false), "Announce your newest YouTube Videos!", "<#channel> <YouTube Channel ID>");

    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length < 2)
            return message(EmbedUtil.error("Invalid parameters", "Use `rc!help youtube` for more info!"));
        if (!RubiconUser.fromUser(invocation.getAuthor()).isPremium())
            return EmbedUtil.message(EmbedUtil.noPremium());
        if (invocation.getMessage().getMentionedChannels().isEmpty())
            return EmbedUtil.message(EmbedUtil.error("No channel", "You forgot to Mention an Channel"));

        Cursor cursor = RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", invocation.getGuild().getIdLong())).run(RubiconBot.getRethink().connection);
        if (!cursor.toList().isEmpty()) {
            RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", invocation.getGuild().getIdLong())).delete().run(RubiconBot.getRethink().connection);
            return message(error("Already registered", "Deleting old Setup"));
        }

        String creator = invocation.getArgs()[1].replace(" ", "");
        Request request = new Request.Builder()
                .url("https://youtube.com/channel/" + creator)
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() != 200) {
                response.close();
                return message(error("Wrong ChannelID", "Your given ChannelID is not Valid.It must be something like UCgez9UZRV7E-JFbo64eCcfg"));
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Message infoMessage = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(new EmbedBuilder().setTitle("Title....").setDescription("Please enter an Message that will be sent whenever an new Video is uploaded! Use %url% for the Video URL and %title% for the Video Title.Markdown **is supported**").setFooter("Will abort in 60 seconds.", null)));
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
                        .with("youchannel", holder.channel.getIdLong())
                        .with("youcreator", holder.creator)
                        .with("lastvideo", 0)
        )).run(RubiconBot.getRethink().getConnection());
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=CHANNEL_ID&maxResults=1&order=date&type=video&key=YOUR_API_KEY".replace("CHANNEL_ID", holder.creator).replace("YOUR_API_KEY", RubiconBot.getConfiguration().getString("google_token")))
                .build();
        String uri = "";
        String title = "";
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (!response.body().toString().isEmpty()) {
                JSONObject object = new JSONObject(response.body().string());
                JSONArray items = (JSONArray) object.get("items");
                JSONObject snippet = (JSONObject) ((JSONObject) items.get(0)).get("snippet");
                JSONObject id = (JSONObject) ((JSONObject) items.get(0)).get("id");
                title = (String) snippet.get("title");
                uri = "http://youtu.be/" + id.get("videoId");
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!uri.equals("")) {
            SafeMessage.sendMessage(holder.channel, description.replace("%url%", uri).replace("%title%", title));
            RubiconBot.getRethink().db.table("youtube").filter(RubiconBot.getRethink().rethinkDB.hashMap("guildId", holder.textChannel.getGuild().getIdLong())).update(RubiconBot.getRethink().rethinkDB.hashMap("lastvideo", uri)).run(RubiconBot.getRethink().connection);
        }
        holder.delete();
        event.getMessage().delete().queue();
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
                    SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.error("Aborted!", "Aborted Setup.")));
                    announceMap.remove(author.getIdLong());
                }
            }, 60000);
        }

        private void delete() {
            announceMap.remove(author.getIdLong());
            timer.cancel();
            SafeMessage.sendMessage(textChannel, EmbedUtil.message(EmbedUtil.success("Success!", "Successfully finished Setup.")));
            infoMessage.delete().queue();
        }
    }

    public static class YouTubeChecker {
        private List<Guild> guildList;
        private Rethink rethink;

        public YouTubeChecker(List<Guild> guilds) {
            this.guildList = guilds;
            this.rethink = RubiconBot.getRethink();

            loadYouTube();
        }

        private synchronized void loadYouTube() {
            Logger.info("Starting YouTube loading thread \"YouTube-Loader\"");
            Thread t = new Thread(() -> {
                for (Guild guild :
                        guildList) {
                    Cursor cursor = rethink.db.table("youtube").filter(rethink.rethinkDB.hashMap("guildId", guild.getIdLong())).run(rethink.connection);
                    List l = cursor.toList();
                    if (l.size() < 1) {
                        Logger.info("No YouTube need to be loaded found. Skipping ...");
                        return;
                    }
                    Logger.info("Finished YouTube loading. Stopping thread");
                }
            });
            t.setName("YouTube-Loader");
            t.start();

        }


    }
}