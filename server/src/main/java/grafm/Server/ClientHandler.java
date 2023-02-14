package grafm.Server;

import java.io.*;
import java.net.*;
import java.util.*;

import grafm.Server.Server;

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
            boolean keepReading = true;
 
            do {
                clientMessage = input.readUTF().trim();
                // Only commands start with a backslash
                if (clientMessage.startsWith("\\")) {
                   String[] splitMessage = clientMessage.split(" "); 
                   switch(server.identifyCommand(splitMessage[0])) {
                        case commands.LOGOUT:
                            keepReading = false;
                            break;
                        case commands.LIST_COMMANDS:
                            output.writeUTF("\\logout - Terminates the connection");
                            output.writeUTF("\\change_nick newName - Changes your nickname");
                            output.writeUTF("\\change_group groupName - Switch to the named chat room");
                            output.writeUTF("\\help - List all available commands");
                            output.writeUTF("\\message userName myMessage - message only the specified user");
                            break;
                        case commands.UNKNOWN:
                            output.writeUTF("Command was not recognized");
                            break;
                        case commands.MESSAGE:
                            if (splitMessage.length > 2) {
                                String message = "";
                                for(int i = 2; i < splitMessage.length; ++i) message += splitMessage[i];
                                if(server.message(splitMessage[1], message)) output.writeUTF("Whispered to: " + splitMessage[1]);
                                else output.writeUTF("ERROR: No such username found");
                            } else output.writeUTF("ERROR: No message to send");
                            break;
                        case commands.CHANGE_GROUP:
                            if (splitMessage.length > 1) this.groupName = splitMessage[1];
                            else output.writeUTF("ERROR: No group specified");
                            break;
                        case commands.CHANGE_NICK:
                            if (splitMessage.length > 1) {
                                this.userName = splitMessage[1];
                                output.writeUTF("Your new nick is: " + this.userName);
                            }
                            else output.writeUTF("ERROR: No nick specified");
                            break;
                   }
                } else server.publishMessage("[" + this.userName + "]: " + clientMessage, this);
            } while (keepReading);
 
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
