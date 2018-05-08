package fun.rubicon.rethink.impl;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import fun.rubicon.rethink.RethinkConnection;
import fun.rubicon.rethink.RethinkConnectionPool;
import fun.rubicon.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class RethinkConnectionPoolImpl implements RethinkConnectionPool {

    private String hostname;
    private String username;
    private int port;
    private String password;
    private String db;
    private int maxConnections;
    private int currentConnections = 0;

    private ArrayList<RethinkConnection> connections;

    public RethinkConnectionPoolImpl(String hostname, String username, int port, String password, String db, int maxConnections) {
        this.hostname = hostname;
        this.username = username;
        this.port = port;
        this.password = password;
        this.db = db;
        this.maxConnections = maxConnections;
        connections = new ArrayList<>();
    }

    @Override
    public Connection getConnection() {
        if(currentConnections == 0) {
            RethinkConnection rc = buildConnection();
            rc.setFree(false);
            rc.setUsages(1);
            connections.add(rc);
            currentConnections++;
            return rc.getConnection();
        } else {
            List<RethinkConnection> freeConnections = connections.stream().filter(rethinkConnection -> rethinkConnection.isFree()).collect(Collectors.toList());
            if(freeConnections.size() > 0) {
                RethinkConnection connection = freeConnections.get(0);
                connections.remove(connection);
                connection.setFree(false);
                connection.setUsages(1);
                connections.add(connection);
            } else {
                if(currentConnections < maxConnections) {
                    RethinkConnection rc = buildConnection();
                    rc.setFree(false);
                    rc.setUsages(1);
                    connections.add(rc);
                    currentConnections++;
                    return rc.getConnection();
                } else {
                    List<RethinkConnection> list = connections;
                    list.sort(Comparator.comparingInt(RethinkConnection::getUsages));
                    RethinkConnection connection = list.get(0);
                    connections.remove(connection);
                    connection.setFree(false);
                    connection.setUsages(connection.getUsages() + 1);
                    connections.add(connection);
                    return connection.getConnection();
                }
            }
        }
        Logger.debug("WTF");
        return null;
    }

    @Override
    public void close() {
        for (RethinkConnection rethinkConnection : connections)
            rethinkConnection.close();
    }

    private RethinkConnection buildConnection() {
        return new RethinkConnectionImpl(true, RethinkDB.r.connection().hostname(hostname).user(username, password).db(db).port(port).connect());
    }
}
