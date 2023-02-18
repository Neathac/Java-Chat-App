package grafm.Client;

import java.io.*;
import java.net.*;

/**
 * Manages the clientside terminal
 * Communicates with the server using Listener and Publisher classes
 */
public class Client {

    private String HOSTNAME;
    private int PORT;
    public String userName;
    public DataInputStream input;
    public DataOutputStream output;

    /**
     * Default constructor sets port to 8089 and Hostname to localhost
     */
    public Client() {
        PORT = 8089;
        HOSTNAME = "localhost";
    }

    /**
     * Constructor allows the setting of port and hostname manually
     * @param port - Port to send requests to
     * @param hostName - Hostname of the server to connect to
     */
    public Client(String port, String hostName) {
        PORT = Integer.parseInt(port);
        HOSTNAME = hostName;
    }

    /**
     * Connects to the server and starts listening for its responses in 2 threads
     */
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

    /**
     * Set the client username locally and send it to server
     * @param userName - Name to be set
     */
    void setUserName(String userName) {
        this.userName = userName;
        try {
            this.output.writeUTF(userName);
        } catch (Exception e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
        
    }
 
    /**
     * A username getter
     * @return - Locally set username
     */
    String getUserName() {
        return this.userName;
    }
}
