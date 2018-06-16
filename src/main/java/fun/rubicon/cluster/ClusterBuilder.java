package fun.rubicon.cluster;

import fun.rubicon.BotLauncher;
import fun.rubicon.RubiconBot;
import fun.rubicon.cluster.event.ClusterListenerAdapter;
import fun.rubicon.cluster.impl.ClusterClientImpl;

import java.util.ArrayList;
import java.util.List;

public class ClusterBuilder {

    private final String host;
    private final int port;
    private final List<ClusterListenerAdapter> listenerAdapterList;

    public ClusterBuilder(String host, int port) {
        this.host = host;
        this.port = port;

        listenerAdapterList = new ArrayList<>();
    }

    public void addListenerAdapter(ClusterListenerAdapter listenerAdapter) {
        listenerAdapterList.add(listenerAdapter);
    }

    public ClusterClient build() throws Exception {
        ClusterClientImpl clusterClient = new ClusterClientImpl(host, port, listenerAdapterList);
        clusterClient.start();
        return clusterClient;
    }
}
