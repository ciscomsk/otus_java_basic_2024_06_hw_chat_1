package ru.otus.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler {
    private final Socket socket;
    private final Server server;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final String username;

    private final static AtomicInteger userCount = new AtomicInteger();

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        userCount.incrementAndGet();
        this.username = "user-" + userCount.get();

        new Thread(() -> {
            try {
                System.out.println("client connected");
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith("/exit")) {
                            sendMessage("/exitok");
                            break;
                        } else if (message.startsWith("/w")) {
                            String[] parsedMsg = message.split("\\s+", 3);
                            sendPrivateMessage(parsedMsg[1], parsedMsg[2]);
                        }
                    } else {
                        server.broadcastMessage(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMessage(String username, String message) {
        ClientHandler handler = server.getHandlerByName(username);
        if (handler != null) {
            try {
                handler.out.writeUTF(username + " [private]: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sendMessage("user with name " + username + " does not exist");
        }
    }

    public void disconnect() {
        server.unsubscribe(username);

        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
