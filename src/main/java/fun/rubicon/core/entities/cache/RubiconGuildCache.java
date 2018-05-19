package fun.rubicon.core.entities.cache;

import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.core.Cache;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.io.deprecated_rethink.Rethink;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Deprecated
public class RubiconGuildCache extends Cache {

    public Rethink rethink;
    private Table table;

    public RubiconGuildCache() {
        this.rethink = RubiconBot.getRethink();
        table = rethink.db.table("guilds");
    }

    public RubiconGuild getGuild(Guild guild) {
        if (contains(guild.getId()))
            return (RubiconGuild) get(guild.getId());
        return (RubiconGuild) update(guild.getId(), retrieveGuild(guild));
    }

    private RubiconGuild retrieveGuild(Guild guild) {
        Cursor cursor = table.filter(rethink.rethinkDB.hashMap("guildId", guild.getId())).run(rethink.getConnection());
        List<?> list = cursor.toList();
        if (list.size() == 0)
            return null;
        HashMap<String, ?> map = (HashMap<String, ?>) list.get(0);
        return new RubiconGuild(guild, map);
    }
}
