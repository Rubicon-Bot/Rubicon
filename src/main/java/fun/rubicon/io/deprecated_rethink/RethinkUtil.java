package fun.rubicon.io.deprecated_rethink;

import com.rethinkdb.gen.exc.ReqlOpFailedError;

/**
 * @author ForYaSee / Yannick Seeger
 */
@Deprecated
public class RethinkUtil {

    private final static String[] tables = {
            "users",
            "members",
            "guilds",
            "mutesettings",
            "joinmessages",
            "joinimages",
            "leavemessages",
            "autochannels",
            "autoroles",
            "punishments",
            "lavanodes",
            "permissions",
            "youtube",
            "rpg_users",
            "rpg_clans",
            "rpg_inventories",
            "verification_settings",
            "verification_users",
            "keys",
            "warn_punishments",
            "warns",
            "reminders",
            "portals",
            "portal_settings",
            "votes",
            "giveaways",
            "giveaways",
            "portal_invites",
    };

    public static void createDefaults(Rethink rethink) {
        rethink.db.config().update(rethink.rethinkDB.hashMap("write_acks", "single")).run(rethink.getConnection());
        for (String table : tables) {
            try {
                rethink.db.tableCreate(table).run(rethink.getConnection());
                rethink.db.table(table).reconfigure().optArg("shards", 3).optArg("replicas", 3).run(rethink.getConnection());
                rethink.db.table(table).optArg("read_mode", "outdated").run(rethink.getConnection());
                rethink.db.table(table).update(rethink.rethinkDB.hashMap("durability", "soft")).run(rethink.getConnection());
            } catch (ReqlOpFailedError ignored) {
                //ignored because its working like -> CREATE TABLE IF NOT EXIST
            }
        }
    }
}
