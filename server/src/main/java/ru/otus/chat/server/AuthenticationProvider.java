package ru.otus.chat.server;

public interface AuthenticationProvider {
    void initialize();
    boolean isAuthenticated(ClientHandler clientHandler, String login, String password);
}
