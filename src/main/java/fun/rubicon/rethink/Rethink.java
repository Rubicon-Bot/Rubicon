package fun.rubicon.rethink;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;
import de.jakobjarosch.rethinkdb.pool.RethinkDBPool;
import de.jakobjarosch.rethinkdb.pool.RethinkDBPoolBuilder;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class Rethink {

    public final RethinkDB rethinkDB;
    private String host;
    private int port;
    private final String dbName;
    private final String user;
    private final String password;
    public RethinkDBPool pool;
    public Db db;
    private Connection connection;

    private int failedConnection = 1;

    public Rethink(String host, int port, String db, String user, String password) {
        this.host = host;
        this.port = port;
        this.dbName = db;
        this.user = user;
        this.password = password;

        rethinkDB = RethinkDB.r;
    }

    public void connect() {
        try {
            if (failedConnection > 1 && failedConnection <= 5) {
                host = RubiconBot.getConfiguration().getString("rethink_host" + failedConnection);
                port = RubiconBot.getConfiguration().getInt("rethink_port" + failedConnection);
            } else if (failedConnection > 5) {
                Logger.error("Can't connect to rethinkdb. Shutdown....");
                System.exit(1);
            }
            RethinkDBPoolBuilder builder = new RethinkDBPoolBuilder();
            builder.hostname(host).port(port).username(user).password(password).maxConnections(100);
            pool = builder.build();
            connection = pool.getConnection();
            db = rethinkDB.db(dbName);
            Logger.info(String.format("RethinkDB connection success (%s)", host));
        } catch (Exception e) {
            failedConnection++;
            connect();
        }
    }

    public static void reanimate() {
        RubiconBot.getRethink().pool.shutdown();
        RubiconBot.connectRethink();
    }

    public RethinkDB getRethinkDB() {
        return rethinkDB;
    }

    public Connection getConnection() {
        return connection;
    }
}
