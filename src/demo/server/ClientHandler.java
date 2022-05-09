package demo.server;

import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler extends Thread {

    Socket client;
    String name;
    Server server;
    BufferedReader br;
    BufferedWriter wr;

    public ClientHandler(Socket s, Server server) throws IOException {
        client = s;
        this.server = server;
        br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        wr = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run() {
        try
        {
            wr.write("Willkommen beim Chat-Server" + "\n\r");
            wr.flush();

            login(br, wr);

            while (true) {
                String answer = br.readLine();

                switch (answer.toLowerCase(Locale.ROOT).split("\\s")[0].trim()) {
                    case "quit":
                        server.logout(this);
                        return;
                    case "list":
                        int i = 0;
                        for (ClientHandler handler : server.getClientList()) {
                            i++;
                            wr.write(i + ": " + handler.name + "\n\r");
                        }
                        wr.flush();
                        break;
                    case "stat":
                        break;
                    case "msg":
                        Pattern pattern = Pattern.compile("msg\\s(.+?)\\s\"(.+?)\"");
                        Matcher matcher = pattern.matcher(answer);

                        if (matcher.matches()) {
                            String receiver = matcher.group(1);
                            String message = matcher.group(2);

                            if (server.isValidUser(receiver)) {
                                server.messageUser(this, receiver, name + " > " + receiver + ": " + message + "\n\r");
                            }
                        } else {
                            wr.write("usage:\n\r" + "msg <user> \"<message>\"\n\r");
                            wr.flush();
                        }
                        break;
                    default:
                        server.messageAll(this, name + ": " + answer);
                        break;
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void message(String message) throws IOException {
        String before = new String(new byte[] { 0x0b, 0x1b, '[', '1', 'A', 0x1b, '7', 0x1b, '[', '1', 'L', '\r' });
        String after = new String(new byte[] { 0x1b, '8', 0x1b, '[', '1', 'B' });
        // wr.write(before + message + after);
        wr.write(message + "\n");
        wr.flush();
    }

    public void login(BufferedReader br, BufferedWriter wr) throws IOException {
        System.out.println("connected to " + client.getRemoteSocketAddress());


        while (true) {
            wr.write("Welchen Spitznamen moechtest du haben:\n");
            wr.flush();
            String answer = br.readLine();

            if (server.login(answer, this)) {
                name = answer;
                return;
            } else {
                wr.write("Der Spitzname \"" + answer + "\" ist leider schon vergeben. Waehle einen anderen." + "\n\r");
                wr.flush();
            }
        }
    }

    public void closeConnection() throws IOException {
        br.close();
        wr.close();
    }
}

