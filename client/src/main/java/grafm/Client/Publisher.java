package grafm.Client;

import java.io.*;
import java.net.*;

public class Publisher implements Runnable {
    private Client client;
    Socket socket;

    public Publisher(Socket socket, Client client) {
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run() {

        String userName = System.console().readLine("\nEnter your name: ");
        client.setUserName(userName);
        String text;

        do {
            text = System.console().readLine("[" + userName + "]: ");
            try {
                client.output.writeUTF(text);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } while (!text.equals("\\logout"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}