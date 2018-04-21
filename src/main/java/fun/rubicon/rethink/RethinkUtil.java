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
            "leavemessages",
            "autochannels",
            "autoroles",
            "punishments",
            "lavanodes",
            "permissions"
    };

    public static void createDefaults(Rethink rethink) {
        for (String table : tables) {
            try {
                rethink.db.tableCreate(table).run(rethink.connection);
            } catch (ReqlOpFailedError ignored) {
                //ignored because its working like -> CREATE TABLE IF NOT EXIST
            }
        }
    }
}
