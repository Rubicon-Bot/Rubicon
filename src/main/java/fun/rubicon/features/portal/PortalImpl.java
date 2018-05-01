package fun.rubicon.features.portal;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class PortalImpl implements Portal {

    private final Rethink rethink;
    private final Table table;
    private final String rawRootGuild;
    private final String rawRootChannel;
    private final HashMap<String, String> rawMembers;
    private final Filter dbPortal;

    public PortalImpl(String rawRootGuild, String rawRootChannel, HashMap<String, String> rawMembers) {
        this.rawRootGuild = rawRootGuild;
        this.rawRootChannel = rawRootChannel;
        this.rawMembers = rawMembers;
        rethink = RubiconBot.getRethink();
        table = rethink.db.table("portals");
        dbPortal = table.filter(rethink.rethinkDB.hashMap("root_guild", rawRootGuild));
    }

    @Override
    public Guild getRootGuild() {
        return RubiconBot.getShardManager().getGuildById(rawRootGuild);
    }

    @Override
    public TextChannel getRootChannel() {
        return RubiconBot.getShardManager().getTextChannelById(rawRootChannel);
    }

    @Override
    public HashMap<Guild, Channel> getMembers() {
        HashMap<Guild, Channel> map = new HashMap<>();
        for (Map.Entry entry : rawMembers.entrySet()) {
            try {
                map.put(RubiconBot.getShardManager().getGuildById((String) entry.getKey()), RubiconBot.getShardManager().getTextChannelById((String) entry.getValue()));
            } catch (Exception ignored) {
            }
        }
        return map;
    }

    @Override
    public void addGuild(String guildId, String channelId) {
        rawMembers.put(guildId, channelId);
        dbPortal.update(rethink.rethinkDB.hashMap("members", rawMembers)).run(rethink.connection);
    }

    @Override
    public void delete() {
        dbPortal.delete().run(rethink.connection);
    }
}
