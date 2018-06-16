package fun.rubicon.cluster.event;

import fun.rubicon.cluster.events.ClusterConnectedEvent;
import fun.rubicon.cluster.events.ClusterDisconnectedEvent;
import fun.rubicon.cluster.events.ClusterMessageReceivedEvent;

public class ClusterListenerAdapter {

    public void onMessageReceived(ClusterMessageReceivedEvent clusterMessageReceivedEvent) {}
    public void onConnected(ClusterConnectedEvent clusterConnectedEvent) {}
    public void onDisconnected(ClusterDisconnectedEvent clusterDisconnectedEvent) {}

    public void onEvent(ClusterEvent event) {
        if(event instanceof ClusterMessageReceivedEvent)
            onMessageReceived((ClusterMessageReceivedEvent) event);
        else if(event instanceof ClusterConnectedEvent)
            onConnected((ClusterConnectedEvent) event);
        else if(event instanceof ClusterDisconnectedEvent)
            onDisconnected((ClusterDisconnectedEvent) event);
    }
}