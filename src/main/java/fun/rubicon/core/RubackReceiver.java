package fun.rubicon.core;

import fun.rubicon.util.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class RubackReceiver implements Runnable {

    private ServerSocket serverSocket;
    private Thread thread;

    private boolean running = false;

    public RubackReceiver() {
        thread = new Thread(this);
        thread.setName("RubackReceiver");
    }

    public synchronized void start() {
        if (!running) {
            running = true;
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(13902);
            serverSocket.setSoTimeout(864000);
            while (running) {
                Socket ruback = serverSocket.accept();
                PrintWriter rubackOut = new PrintWriter(ruback.getOutputStream(), true);
                rubackOut.println(RubackCommands.OK.getMessage());
                ruback.close();
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private enum RubackCommands {
        OK("Status: OK");

        private String message;

        RubackCommands(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}