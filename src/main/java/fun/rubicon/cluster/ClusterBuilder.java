package fun.rubicon.cluster;

import fun.rubicon.cluster.impl.ClusterImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ClusterBuilder {

    private final List<ClusterHost> hostList;

    public ClusterBuilder() {
        hostList = new ArrayList<>();
    }

    public void addHost(ClusterHost host) {
        hostList.add(host);
    }

    public void addHost(String host, String password) {
        addHost(new ClusterHost(host, password));
    }

    public void addHosts(ClusterHost... hosts) {
        hostList.addAll(Arrays.asList(hosts));
    }

    public void removeHost(ClusterHost host) {
        hostList.remove(host);
    }

    public void removeHost(String host, String password) {
        removeHost(new ClusterHost(host, password));
    }

    public Cluster build() throws IOException {
        return new ClusterImpl(hostList);
    }
}
