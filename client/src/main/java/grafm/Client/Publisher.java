package grafm.Client;

import java.io.*;
import java.net.*;

public class Publisher implements Runnable {
    private Client client;
    Socket socket;
    String userName;

    /**
     * Constructor stores the open socket connection instance and caller Client instance
     * @param socket - Open socket connection to store
     * @param client - Caller Client to store
     */
    public Publisher(Socket socket, Client client) {
        this.client = client;
        this.socket = socket;
    }

    /**
     * While thread is running, forwards user console input to the server indefinitely
     */
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
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Server closed the connection");
            System.exit(0);
        }
    }
}
