package fun.rubicon.rethink;

import com.rethinkdb.net.Connection;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface RethinkConnectionPool {

    Connection getConnection();

    void close();
}
