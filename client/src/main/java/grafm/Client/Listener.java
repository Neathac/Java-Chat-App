package grafm.Client;

import java.io.*;
import java.net.*;
 
public class Listener implements Runnable {
    private Client client;
 
    /**
     * Constructor stores the instance of the caller
     * @param client - Client instance to be stored
     */
    public Listener(Client client) {
        this.client = client;
    }
 
    /**
     * While thread is running, continually listen to server responses and treat them
     */
    @Override
    public void run() {
        while (true) {
            try {
                String response = client.input.readUTF();
                if (response.startsWith("Your new nick is: ")) {
                    String[] splitMessage = response.split(" "); 
                    String message = "";
                    for(int i = 4; i < splitMessage.length; ++i) message += splitMessage[i] + " ";
                    this.client.userName = message.trim();
                }
                System.out.println("\n" + response);
                
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                System.out.println("Server ended connection");
                break;
            }
        }
    }
}
