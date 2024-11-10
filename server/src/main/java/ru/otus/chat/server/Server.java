package ru.otus.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final int port;
    private final Map<String, ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new HashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("server running on port: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                subscribe(handler.getUsername(), handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(String username, ClientHandler clientHandler) {
        clients.put(username, clientHandler);
    }

    public synchronized void unsubscribe(String username) {
        clients.remove(username);
    }

    public synchronized void broadcastMessage(String message) {
        clients.values().forEach(client -> client.sendMessage(message));
    }

    public ClientHandler getHandlerByName(String username) {
        return clients.get(username);
    }
}
