package ru.otus.chat.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private class User {
        private String login;
        private String password;
        private String username;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<User> users;

    public InMemoryAuthenticationProvider() {
        users = new ArrayList<>();
        users.add(new User("login1", "password1", "username1"));
        users.add(new User("qwe", "qwe", "qwe1"));
        users.add(new User("asf", "asd", "asd1"));
        users.add(new User("zxc", "zxc", "zxc1"));
    }

    @Override
    public void initialize() {
        System.out.println("authentication service started: in memory mode");
    }

    @Override
    public synchronized boolean isAuthenticated(ClientHandler clientHandler, String login, String password) {
        String authName = getUsernameByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("incorrect login or password");
            return false;
        }



        return false;
    }

    private String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.username;
            }
        }
        return null;
    }
}
