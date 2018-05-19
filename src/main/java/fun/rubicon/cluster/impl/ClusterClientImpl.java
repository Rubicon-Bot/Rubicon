package fun.rubicon.cluster.impl;

import fun.rubicon.RubiconBot;
import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.events.ClusterClientDisconnectedEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.OffsetDateTime;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ClusterClientImpl implements ClusterClient {

    private final Socket socket;

    public ClusterClientImpl(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void write(String s) {
        try (PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
            writer.write(s);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
            RubiconBot.getInstance().getEventManager().call(new ClusterClientDisconnectedEvent(this, OffsetDateTime.now()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
