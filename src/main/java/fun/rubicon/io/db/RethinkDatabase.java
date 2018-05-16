package fun.rubicon.io.db;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import fun.rubicon.core.ShutdownManager;
import fun.rubicon.entities.User;
import fun.rubicon.entities.impl.UserImpl;
import fun.rubicon.io.Data;
import fun.rubicon.provider.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RethinkDatabase {

    public final RethinkDB r = RethinkDB.r;
    private final Logger logger = LoggerFactory.getLogger(RethinkDatabase.class);
    private int connectionAttempt;
    private Connection connection;

    public RethinkDatabase() {
        connectionAttempt = 0;
        connect();
    }

    private void connect() {
        String dbHost = Data.cfg().has("rethinkdb_host") ? (String) Data.cfg().getElementFromArray("rethinkdb_host", connectionAttempt) : null;
        String dbUser = Data.cfg().has("rethinkdb_user") ? (String) Data.cfg().getElementFromArray("rethinkdb_user", connectionAttempt) : null;
        int dbPort = Data.cfg().has("rethinkdb_port") ? Data.cfg().getElementFromArray("rethinkdb_port", connectionAttempt) == null ? 0 : (int) Data.cfg().getElementFromArray("rethinkdb_port", connectionAttempt) : 0;
        String dbPassword = Data.cfg().has("rethinkdb_password") ? (String) Data.cfg().getElementFromArray("rethinkdb_password", connectionAttempt) : null;
        String db = Data.cfg().has("rethinkdb_db") ? Data.cfg().getString("rethinkdb_db") : null;

        if (dbHost == null || dbUser == null || dbPort == 0 || dbPassword == null || db == null)
            ShutdownManager.shutdown(RethinkDatabase.class, "One or more of the connection properties are null.");
        try {
            assert dbHost != null;
            assert dbUser != null;
            assert dbPassword != null;
            assert db != null;
            connection = r.connection().hostname(dbHost).port(dbPort).user(dbUser, dbPassword).db(db).connect();
        } catch (Exception e) {
            logger.error(String.format("Can't create a connection to %s", dbHost), e);
            connectionAttempt++;
            connect();
        }
    }

    //Entity Getter
    public User getUser(@Nonnull net.dv8tion.jda.core.entities.User jdaUser) {
        User user = r.table(UserImpl.TABLE).get(String.valueOf(jdaUser.getIdLong())).run(connection, User.class);
        if(user == null)
            user = new UserImpl(jdaUser, "No bio set.", 0, "en-US", null, 0, new HashMap<>());
        UserProvider.getCache().put(jdaUser.getIdLong(), user);
        return user;
    }

    public void save(@Nonnull RethinkDataset dataset) {
        checkConnection();
        logger.debug(String.format("Saving %s in %s", dataset.getId(), dataset.getTable()));
        r.table(dataset.getTable()).insert(dataset).optArg("conflict", "replace").runNoReply(connection);
    }

    public void delete(@Nonnull RethinkDataset dataset) {
        checkConnection();
        logger.debug(String.format("Deleting %s from %s", dataset.getId(), dataset.getTable()));
        r.table(dataset.getTable()).get(dataset.getId()).delete().runNoReply(connection);
    }

    public Connection getConnection() {
        checkConnection();
        return connection;
    }

    public void closeConnection() {
        if (connection != null)
            connection.close();
    }

    private void checkConnection() {
        if (connection == null) {
            logger.warn("Connection is null. Trying to reconnect...");
            connect();
        }
    }
}
