package demo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> activeClientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(22433)) {
            while (true) {
                System.out.println("ready to connect");

                Socket client = server.accept();

                ClientHandler clientHandler = new ClientHandler(client, this);
                clientHandler.start();
                activeClientHandlers.add(clientHandler);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public boolean login(String username, ClientHandler clientHandler) {
        // xy hat den raum betreten
        for (ClientHandler handler : activeClientHandlers) {
            if (handler.name != null) {
                if (handler.name.equals(username)) {
                    return false;
                }
            }
        }

        messageAll(clientHandler, username + " hat den Raum betreten.");
        return true;
    }

    public void logout(ClientHandler client) {
        messageAll(client, client.name + " hat den Raum verlassen.");
        try {
            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activeClientHandlers.remove(client);
    }

    public void messageAll(ClientHandler sender,String message) {
        for (ClientHandler handler : activeClientHandlers) {
            if (handler.equals(sender)) {
                continue;
            }

            try {
                handler.message(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageUser(ClientHandler sender, String receiver, String message) {
        if (isValidUser(receiver)) {
            for (ClientHandler handler : activeClientHandlers) {
                if (handler.name.equalsIgnoreCase(receiver)) {
                    try {
                        System.out.println(sender.name + " > " + receiver);
                        handler.message(message);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public boolean isValidUser(String username) {
        for (ClientHandler handler : activeClientHandlers) {
            if (handler.name.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public List<ClientHandler> getClientList() {
        return activeClientHandlers;
    }
}

