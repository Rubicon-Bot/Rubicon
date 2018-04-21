package fun.rubicon.util;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class Rethink {

    public final RethinkDB rethinkDB;
    private final String host;
    private final int port;
    private final String dbName;
    public Connection connection;
    public Db db;

    public Rethink(String host, int port, String db) {
        this.host = host;
        this.port = port;
        this.dbName = db;

        rethinkDB = RethinkDB.r;
    }

    public void connect() {
        connection = rethinkDB.connection().hostname(host).port(port).connect();
        db = rethinkDB.db(dbName);
        Logger.info("RethinkDB connection success");
    }

    public void createTable(String name) {
        rethinkDB.db(db).tableCreate(name).run(connection);
    }

    public RethinkDB getRethinkDB() {
        return rethinkDB;
    }

    public Connection getConnection() {
        return connection;
    }
}
