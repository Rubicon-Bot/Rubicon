package fun.rubicon.io;

import fun.rubicon.core.ShutdownManager;
import fun.rubicon.io.db.RethinkDatabase;
import fun.rubicon.util.RubiconInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class Data {

    private static final Logger logger = LoggerFactory.getLogger(Data.class);

    private static Config config;
    private static RethinkDatabase rethinkDatabase;

    /**
     * @return the Rubicon {@link Config} instance
     */
    public static Config config() {
        if(config == null)
            ShutdownManager.shutdown(Data.class, "Config instance is null.");
        return config;
    }

    /**
     * {@link Data#config} alias
     */
    public static Config cfg() {
        return config();
    }

    /**
     * @return the Rubicon {@link RethinkDatabase} instance
     */
    public static RethinkDatabase db() {
        if(rethinkDatabase == null)
            ShutdownManager.shutdown(Data.class, "RethinkDB instance is null.");
        return rethinkDatabase;
    }

    public static void init() {
        if(config == null) {
            config = new Config(RubiconInfo.CONFIG_FILE);
            setConfigDefaults();
        }
        if(rethinkDatabase == null)
            rethinkDatabase = new RethinkDatabase();
    }

    private static void setConfigDefaults() {
        Data.cfg().setDefault("bot_token", "myToken");
        Data.cfg().setDefault("cluster_password", "myPassword");
        Data.cfg().setDefault("rethinkdb_host", new ArrayList<>());
        Data.cfg().setDefault("rethinkdb_user", new ArrayList<>());
        Data.cfg().setDefault("rethinkdb_port", new ArrayList<>());
        Data.cfg().setDefault("rethinkdb_password", new ArrayList<>());
        Data.cfg().setDefault("rethinkdb_db", "rubicon");
    }
}
