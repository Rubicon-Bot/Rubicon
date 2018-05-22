package fun.rubicon.cluster.impl;

import fun.rubicon.RubiconBot;
import fun.rubicon.cluster.Cluster;
import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.ClusterHost;
import fun.rubicon.cluster.events.ClusterClientConnectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClusterImpl implements Cluster, Runnable {

    private final Logger logger = LoggerFactory.getLogger(ClusterImpl.class);
    private final Thread serverThread;
    private final List<ClusterHost> hosts;
    //private final List<ClusterClient> clientSockets;
    private final List<ClusterClient> hostSockets;
    private final ServerSocket serverSocket;
    private boolean isRunning;

    public ClusterImpl(List<ClusterHost> hosts) throws IOException {
        this.hosts = hosts;
        hostSockets = new ArrayList<>();
        serverSocket = new ServerSocket(13902);
        serverThread = new Thread(this);
        isRunning = true;

        serverThread.start();
    }

    /**
     * Connection Receiver
     */
    @Override
    public void run() {
        while (isRunning) {
            try {
                Socket client = serverSocket.accept();
                ClusterClient clusterClient = new ClusterClientImpl(client);
                hostSockets.add(clusterClient);
                logger.debug(String.format("[Client connected to cluster] %s(%s)", client.getInetAddress().getHostAddress(), client.getInetAddress().getHostName()));
                RubiconBot.getInstance().getEventManager().call(new ClusterClientConnectedEvent(clusterClient, OffsetDateTime.now()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (!isRunning)
            return;
        isRunning = false;

        for (ClusterClient clusterClient : hostSockets) {
            clusterClient.close();
        }
    }

    @Override
    public synchronized void start() {
        if (isRunning)
            return;
        isRunning = true;
        serverThread.start();
    }
}
