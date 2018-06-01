package fun.rubicon.features.poll;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconPoll;
import fun.rubicon.io.deprecated_rethink.Rethink;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Rittmeister / Schlaubi
 * @license GNU General Public License v3.0
 */
public class PollManager {

    private HashMap<Guild, RubiconPoll> polls = new HashMap<>();
    private boolean running = false;
    Rethink rethink = RubiconBot.getRethink();

    public synchronized void loadPolls() {
        new Thread(() -> {
            Cursor cursor = rethink.db.table("votes").run(rethink.getConnection());
            for (Object obj : cursor) {
                Map map = (Map) obj;
                Guild guild = RubiconBot.getShardManager().getGuildById((String) map.get("guild"));
                if (guild == null) {
                    deletePoll((String) map.get("guild"));
                    continue;
                }
                RubiconPoll poll = new RubiconPoll((String) map.get("creator"), (String) map.get("heading"), (List<String>) map.get("answers"), (HashMap<String, String>) map.get("pollmsgs"), (HashMap<String, String>) map.get("votes"), (HashMap<String, String>) map.get("reacts"), guild);
                polls.put(guild, poll);
            }
        }, "PollLoadingThread").start();
    }


    private Guild getGuild(String id) {
        return RubiconBot.getShardManager().getGuildById(id);
    }

    public HashMap<Guild, RubiconPoll> getPolls() {
        return polls;
    }

    public RubiconPoll getPollByGuild(Guild guild) {
        return polls.get(guild);
    }

    public boolean pollExists(Guild guild) {
        Cursor cursor = rethink.db.table("votes").filter(rethink.rethinkDB.hashMap("guild", guild.getId())).run(rethink.getConnection());
        return !cursor.toList().isEmpty();
    }

    public void replacePoll(RubiconPoll poll, Guild guild) {
        getPolls().replace(guild, poll);
        poll.savePoll();
    }

    public void deletePoll(String guildId) {
        rethink.db.table("votes").filter(rethink.rethinkDB.hashMap("guild", guildId)).delete().run(rethink.getConnection());
    }


}
