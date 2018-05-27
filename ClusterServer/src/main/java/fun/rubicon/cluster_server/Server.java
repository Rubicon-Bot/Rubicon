package fun.rubicon.cluster_server;

import fun.rubicon.cluster_server.cluster.ClusterServer;
import fun.rubicon.cluster_server.cluster.command.ClusterCommandManager;
import fun.rubicon.cluster_server.event_impls.ClientConnectedEvent;
import fun.rubicon.cluster_server.util.Config;
import lombok.Getter;

public class Server {

    @Getter private static Server instance;
    @Getter private final Config config;
    @Getter private final ClusterCommandManager clusterCommandManager;
    @Getter private final ClusterServer clusterServer;

    public Server() {
        instance = this;
        config = new Config("server-config.json");
        config.set("port", 13902);

        clusterCommandManager = new ClusterCommandManager();
        ClusterBuilder clusterBuilder = new ClusterBuilder(config.getInt("port"));
        clusterBuilder.addListenerAdapter(clusterCommandManager);
        clusterBuilder.addListenerAdapter(new ClientConnectedEvent());
        try {
            clusterServer = clusterBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Can't start server. Shutdown.");
        }
    }
}
