package grafm;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Server server;
    DataInputStream input;
    DataOutputStream output;

    String groupName;
    String userName;

    public ClientHandler(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        this.groupName = "general";
        try {
            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

    @Override
    public void run() {
        try {
            userName = input.readUTF();

            server.publishMessage("New user connected: " + userName, this);
 
            String clientMessage;
 
            do {
                clientMessage = input.readUTF();
                server.publishMessage("[" + userName + "]: " + clientMessage, this);
 
            } while (!clientMessage.equals("\\logout"));
 
            server.removeUser(this);
            socket.close();
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void printUsers() throws IOException {
        if (server.hasUsers(groupName)) {
            output.writeUTF("Connected users: " + server.getUserNames(groupName));
        } else {
            output.writeUTF("No other users connected");
        }
    }
}
