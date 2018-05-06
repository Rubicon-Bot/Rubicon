package fun.rubicon.rethink;

import com.rethinkdb.gen.exc.ReqlOpFailedError;

/**
 * @author ForYaSee / Yannick Seeger
 */
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
            "giveaways"
            "giveaways",
            "portal_invites",
    };

    public static void createDefaults(Rethink rethink) {
        for (String table : tables) {
            try {
                rethink.db.tableCreate(table).run(rethink.connection);
                rethink.db.table(table).reconfigure().optArg("shards", 3).optArg("replicas", 3).run(rethink.connection);
            } catch (ReqlOpFailedError ignored) {
                //ignored because its working like -> CREATE TABLE IF NOT EXIST
            }
        }
    }
}
