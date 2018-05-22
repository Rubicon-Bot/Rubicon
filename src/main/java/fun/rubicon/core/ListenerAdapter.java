package fun.rubicon.core;

import fun.rubicon.cluster.events.ClusterClientConnectedEvent;
import fun.rubicon.cluster.events.ClusterClientDisconnectedEvent;
import fun.rubicon.cluster.events.ClusterConnectedEvent;
import fun.rubicon.cluster.events.ClusterDisconnectedEvent;
import fun.rubicon.listener.Event;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ListenerAdapter {

    public void onGenericEvent(Event event) {
    }

    //CLUSTER
    public void onClusterConnected(ClusterConnectedEvent clusterConnectedEvent) {
    }

    public void onClusterDisconnected(ClusterDisconnectedEvent clusterDisconnectedEvent) {
    }

    public void onClusterClientConnected(ClusterClientConnectedEvent clusterClientConnectedEvent) {
    }

    public void onClusterClientDisconnectedEvent(ClusterClientDisconnectedEvent clusterDisconnectedEvent) {
    }

    public void onEvent(Event event) {
        new Thread(() -> onGenericEvent(event)).start(); //Asynchronous because generic
        if (event instanceof ClusterConnectedEvent)
            onClusterConnected((ClusterConnectedEvent) event);
        else if (event instanceof ClusterDisconnectedEvent)
            onClusterDisconnected((ClusterDisconnectedEvent) event);
        else if (event instanceof ClusterClientConnectedEvent)
            onClusterClientConnected((ClusterClientConnectedEvent) event);
        else if (event instanceof ClusterClientDisconnectedEvent)
            onClusterClientDisconnectedEvent((ClusterClientDisconnectedEvent) event);
    }
}
