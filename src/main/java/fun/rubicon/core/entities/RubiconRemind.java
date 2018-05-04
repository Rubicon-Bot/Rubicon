package fun.rubicon.core.entities;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class RubiconRemind {

    public static HashMap<User, Timer> timerStorage = new HashMap<>();

    private Rethink rethink = RubiconBot.getRethink();

    private User author;
    private Date remindDate;
    private String remindMessage;
    private boolean exists = false;

    /**
     * Retrive the user's current reminder
     * @param user The reminder's user
     */
    public RubiconRemind(User user){
        Cursor cursor = rethink.db.table("reminders").filter(rethink.rethinkDB.hashMap("userId", user.getId())).run(rethink.connection);
        List list = cursor.toList();
        if(list.isEmpty())
            return;
        Map map = (Map) list.get(0);
        this.author = RubiconBot.getShardManager().getUserById((String) map.get("userId"));
        this.remindDate = new Date(Long.parseLong((String) map.get("remindDate")));
        this.remindMessage = (String) map.get("remindMessage");
        if(author != null && remindDate != null && remindMessage != null)
            exists = true;
    }

    /**
     * Creates a new reminder
     * @param author The author of the reminder.
     * @param remindDate The date when the reminder should expire
     * @param remindMessage The message that should be sent when the remind expires
     */
    public RubiconRemind(User author, Date remindDate, String remindMessage){
        this.author = author;
        this.remindDate = remindDate;
        this.remindMessage = remindMessage;
        rethink.db.table("reminders").insert(rethink.rethinkDB.hashMap("userId", this.author.getId()).with("remindDate", String.valueOf(this.remindDate.getTime())).with("remindMessage", this.remindMessage)).run(rethink.connection);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                delete();
                author.openPrivateChannel().complete().sendMessage(remindMessage).queue();
            }
        }, this.remindDate);
        timerStorage.put(this.author, timer);
    }

    public User getAuthor() {
        return author;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public String getRemindMessage() {
        return remindMessage;
    }

    public boolean exists() {
        return exists;
    }

    public void cancel(){
        timerStorage.get(author).cancel();
        delete();
    }

    public void delete(){
        rethink.db.table("reminders").filter(rethink.rethinkDB.hashMap("userId", author.getId())).delete().run(rethink.connection);
        timerStorage.remove(author);
    }

    private void schedule(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                delete();
                author.openPrivateChannel().complete().sendMessage(remindMessage).queue();
            }
        }, this.remindDate);
        timerStorage.put(this.author, timer);
    }

    public static void loadReminders(){
        new Thread(() -> {
            Cursor cursor = RubiconBot.getRethink().db.table("reminders").run(RubiconBot.getRethink().connection);
            for (Object obj : cursor) {
                Map map = (Map) obj;
                User user = RubiconBot.getShardManager().getUserById((String) map.get("userId"));
                if(user == null) continue;
                new RubiconRemind(user).schedule();
            }
        }, "RemindLoaderThread").start();
    }
}
