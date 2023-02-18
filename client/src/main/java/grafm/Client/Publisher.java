package grafm.Client;

import java.io.*;
import java.net.*;

public class Publisher implements Runnable {
    private Client client;
    Socket socket;
    String userName;

    public Publisher(Socket socket, Client client) {
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run() {

        userName = System.console().readLine("\nEnter your name: ");
        client.setUserName(userName);
        String text;

        do {
            text = System.console().readLine("[" + userName + "]: ");
            if (text != null) {
               try {
                    client.output.writeUTF(text);
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
        } while (!text.equals("\\logout"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server closed the connection");
        }
    }
}
