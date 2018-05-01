package fun.rubicon.features.portal;

import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;

import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class PortalManager {

    private final Rethink rethink;

    public PortalManager() {
        rethink = RubiconBot.getRethink();
    }

    public String getSearchingGuild(String exclude) {
        Cursor cursor = rethink.db.table("guilds").filter(rethink.rethinkDB.hashMap("portal", "SEARCH")).run(rethink.connection);
        List<HashMap> list = cursor.toList();
        for (HashMap<String, Object> map : list) {
            String id = (String) map.get("guildId");
            if (id.equals(exclude))
                continue;
            return id;
        }
        return null;
    }

    public Portal createPortal(String rootGuildId, String rootChannelId) {
        rethink.db.table("portals").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("root_guild", rootGuildId)
                .with("root_channel", rootChannelId)
                .with("members",
                        rethink.rethinkDB.hashMap()
                ))).run(rethink.connection);
        Cursor cursor = rethink.db.table("portals").filter(rethink.rethinkDB.hashMap("root_guild", rootGuildId)).run(rethink.connection);
        HashMap<String, ?> map = (HashMap<String, ?>) cursor.toList().get(0);
        return new PortalImpl(rootGuildId, rootChannelId, (HashMap<String, String>) map.get("members"));
    }

    public Portal getPortal(String guildId) {

    }
}
