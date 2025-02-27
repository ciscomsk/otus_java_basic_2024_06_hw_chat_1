package ru.otus.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Client() throws IOException {
        this.socket = new Socket("localhost", 8189);
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        Scanner sc = new Scanner(System.in);

        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith("/exitok")) {
                            break;
                        }
                    } else {
                        System.out.println(message);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();

        while (true) {
            String message = sc.nextLine();
            out.writeUTF(message);

            if (message.startsWith("/exit")) {
                break;
            }
        }
    }

    public void disconnect() {
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
