package fun.rubicon.features.portal;

import com.rethinkdb.gen.ast.Filter;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Cursor;
import fun.rubicon.RubiconBot;
import fun.rubicon.rethink.Rethink;
import fun.rubicon.rethink.RethinkHelper;
import net.dv8tion.jda.core.entities.Guild;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class GuildPortalManager extends RethinkHelper {

    private final Guild guild;
    private final Rethink rethink;
    private final Table table;
    private final Filter dbGuild;

    public GuildPortalManager(Guild guild) {
        this.guild = guild;
        this.rethink = RubiconBot.getRethink();
        this.table = rethink.db.table("portal_settings");
        this.dbGuild = table.filter(rethink.rethinkDB.hashMap("guildId", guild.getId()));

        createIfNotExist();
    }

    public void setInvites(boolean state) {
        dbGuild.update(rethink.rethinkDB.hashMap("invites", state)).run(rethink.connection);
    }

    public void setEmbeds(boolean state) {
        dbGuild.update(rethink.rethinkDB.hashMap("embeds", state)).run(rethink.connection);
    }

    public boolean hasInvitesEnabled() {
        return getBoolean(retrieve(), "invites");
    }

    public boolean hasEmbedsEnabled() {
        return getBoolean(retrieve(), "embeds");
    }

    public void delete() {
        dbGuild.delete().run(rethink.connection);
    }

    private boolean exist() {
        return retrieve().toList().size() != 0;
    }

    private void createIfNotExist() {
        if (exist())
            return;
        rethink.db.table("portal_settings").insert(rethink.rethinkDB.array(rethink.rethinkDB.hashMap("guildId", guild.getId()))).run(rethink.connection);
    }

    private Cursor retrieve() {
        return dbGuild.run(rethink.connection);
    }
}
