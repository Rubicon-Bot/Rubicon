package fun.rubicon.core.entities;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.commands.tools.CommandGiveaway;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class RubiconGiveaway {

    private static HashMap<User, Timer> timerStorage = new HashMap<>();

    private Rethink rethink = RubiconBot.getRethink();

    private User author;
    private Date expirationDate;
    private String prize;
    private List<String> users;
    private String guildId;
    private String channelId;
    private String messageId;
    private int winnerCount;
    private boolean exists = false;


    /**
     * Retrive the user's current giveaway by user
     *
     * @param user The giveaway's user
     */
    public RubiconGiveaway(User user) {
        Cursor cursor = rethink.db.table("giveaways").filter(rethink.rethinkDB.hashMap("userId", user.getId())).run(rethink.getConnection());
        List list = cursor.toList();
        if (list.isEmpty())
            return;
        Map map = (Map) list.get(0);
        this.author = RubiconBot.getShardManager().getUserById((String) map.get("userId"));
        this.expirationDate = new Date(Long.parseLong((String) map.get("expirationDate")));
        this.prize = (String) map.get("prize");
        this.users = (List<String>) map.get("users");
        this.guildId = (String) map.get("guildId");
        this.channelId = (String) map.get("channelId");
        this.messageId = (String) map.get("messageId");
        this.winnerCount = Integer.parseInt((String) map.get("winnerCount"));
        if (author != null && expirationDate != null && prize != null)
            exists = true;
    }

    /**
     * Retrive the user's current giveaway by user
     *
     * @param msg The giveaway's messageId
     */
    public RubiconGiveaway(String msg) {
        Cursor cursor = rethink.db.table("giveaways").filter(rethink.rethinkDB.hashMap("messageId", msg)).run(rethink.getConnection());
        List list = cursor.toList();
        if (list.isEmpty())
            return;
        Map map = (Map) list.get(0);
        this.author = RubiconBot.getShardManager().getUserById((String) map.get("userId"));
        this.expirationDate = new Date(Long.parseLong((String) map.get("expirationDate")));
        this.prize = (String) map.get("prize");
        this.users = (List<String>) map.get("users");
        this.guildId = (String) map.get("guildId");
        this.channelId = (String) map.get("channelId");
        this.messageId = (String) map.get("messageId");
        this.winnerCount = Integer.parseInt((String) map.get("winnerCount"));
        if (author != null && expirationDate != null && prize != null)
            exists = true;
    }


    /**
     * Creates a new giveaway
     *
     * @param author         The author of the giveaway.
     * @param expirationDate The date when the giveaway should expire
     * @param prize          The message that should be in the Giveaway message
     */
    public RubiconGiveaway(User author, Date expirationDate, String prize, TextChannel channel, int winnerCount) {
        this.author = author;
        this.expirationDate = expirationDate;
        this.prize = prize;
        this.users = new ArrayList<>();
        this.guildId = channel.getId();
        this.channelId = channel.getId();
        this.winnerCount = winnerCount;
        Message msg = SafeMessage.sendMessageBlocking(channel, CommandGiveaway.formatGiveaway(this).setTitle("Giveaway Created").build());
        this.messageId = msg.getId();
        msg.addReaction("\uD83C\uDFC6").complete();

        rethink.db.table("giveaways").insert(rethink.rethinkDB.hashMap("userId", this.author.getId()).with("expirationDate", String.valueOf(this.expirationDate.getTime())).with("prize", this.prize).with("guildId", guildId).with("users", users).with("channelId", channelId).with("messageId", messageId).with("winnerCount", String.valueOf(winnerCount))).run(rethink.getConnection());
        schedule();
    }



    public static void handleReaction(MessageReactionAddEvent event) {
        if(event.getUser().isBot()) return;
        if(!event.getReactionEmote().getName().equals("\uD83C\uDFC6"))
            return;
        RubiconGiveaway giveaway = new RubiconGiveaway(event.getMessageId());
        if(!giveaway.isExists()) return;
        if(giveaway.hasUser(event.getUser())) return;
        if(giveaway.getAuthor().equals(event.getUser())) return;
        giveaway.addUser(event.getUser());
        event.getUser().openPrivateChannel().complete().sendMessage("Du nicht dumm bist #YODA").queue();

    }

    public void schedule() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                users = new RubiconGiveaway(author).getUsers();
                EmbedBuilder msg = EmbedUtil.error();
                if(users.isEmpty())
                    msg = EmbedUtil.error();
                StringBuilder userNames = new StringBuilder();
                for(int i = 0; i< winnerCount; i++){
                    User user = RubiconBot.getShardManager().getUserById(users.get(ThreadLocalRandom.current().nextInt(users.size())));
                    if(!users.isEmpty())
                        userNames.append(user.getName()).append(", ");
                    if(!userNames.toString().equals(""))
                        userNames.replace(userNames.lastIndexOf(","), userNames.lastIndexOf(",") + 1, "");
                    msg = EmbedUtil.info("WINNER", userNames.toString() + " has won `" + prize + "` from " + author.getAsMention());
                }
                SafeMessage.sendMessage(RubiconBot.getShardManager().getTextChannelById(getChannelId()), msg.build());

                delete();
            }
        }, this.expirationDate);
        timerStorage.put(this.author, timer);
    }

    private RubiconGiveaway addUser(User user) {
        this.users.add(user.getId());
        rethink.db.table("giveaways").filter(rethink.rethinkDB.hashMap("userId", this.author.getId())).update(rethink.rethinkDB.hashMap("users", this.users)).run(rethink.getConnection());
        return this;
    }

    private boolean hasUser(User user) {
        return this.users.contains(user.getId());
    }

    public void cancel() {
        timerStorage.get(author).cancel();
        delete();
    }

    public void delete() {
        rethink.db.table("giveaways").filter(rethink.rethinkDB.hashMap("userId", author.getId())).delete().run(rethink.getConnection());
        timerStorage.remove(author);
    }

    public static HashMap<User, Timer> getTimerStorage() {
        return timerStorage;
    }


    public User getAuthor() {
        return author;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getPrize() {
        return prize;
    }

    public boolean isExists() {
        return exists;
    }

    public List<String> getUsers() {
        return users;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public static void loadGiveaways() {
        new Thread(() -> {
            Cursor cursor = RubiconBot.getRethink().db.table("giveaways").run(RubiconBot.getRethink().getConnection());
            for (Object obj : cursor) {
                Map map = (Map) obj;
                User user = RubiconBot.getShardManager().getUserById((String) map.get("userId"));
                if (user == null) continue;
                new RubiconGiveaway(user).schedule();
            }
        }, "RemindLoaderThread").start();
    }
}
