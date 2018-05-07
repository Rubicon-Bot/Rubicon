package fun.rubicon.rethink;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;
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
    public RethinkConnectionPool pool;
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
            RethinkConnectionPoolBuilder builder = new RethinkConnectionPoolBuilder();
            builder.hostname(host).port(port).username(user).password(password).maxConnections(10);
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
        RubiconBot.getRethink().pool.close();
        RubiconBot.connectRethink();
    }

    public RethinkDB getRethinkDB() {
        return rethinkDB;
    }

    public Connection getConnection() {
        return pool.getConnection();
    }
}
