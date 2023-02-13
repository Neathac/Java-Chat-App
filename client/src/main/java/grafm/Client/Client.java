package grafm.Client;

import java.io.*;
import java.net.*;

public class Client {
    
    private final String HOSTNAME = "localhost";
    private final int PORT = 8089;
    private String userName;
    public DataInputStream input;
    public DataOutputStream output;
 
    public void connect() {
        try {
            Socket socket = new Socket(InetAddress.getByName(HOSTNAME), PORT);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to the chat server");
            System.out.println("Currently in the room: general");
 
            new Thread(new Listener(this)).start();
            new Thread(new Publisher(socket, this)).start();
 
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    void setUserName(String userName) {
        this.userName = userName;
    }
 
    String getUserName() {
        return this.userName;
    }
}
