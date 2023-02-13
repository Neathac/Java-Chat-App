package grafm.Client;

import java.io.*;
import java.net.*;
 
public class Listener implements Runnable {
    private Client client;
 
    public Listener(Client client) {
        this.client = client;
    }
 
    @Override
    public void run() {
        while (true) {
            try {
                String response = client.input.readUTF();
                System.out.println("\n" + response);
 
                // prints the username after displaying the server's message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
