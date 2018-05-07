package fun.rubicon.rethink;

import com.rethinkdb.net.Connection;

import java.util.concurrent.TimeoutException;

/**
 * @author ForYaSee / Yannick Seeger
 */
public interface RethinkConnection {

    boolean isFree();

    void setFree(boolean isFree);

    void close();

    void connect() throws TimeoutException;

    int getUsages();

    void setUsages(int usages);

    Connection getConnection();
}
