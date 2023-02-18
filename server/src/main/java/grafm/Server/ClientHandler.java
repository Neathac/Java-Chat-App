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

            server.publishMessage("New user connected: " + userName, this, this.groupName);
 
            String clientMessage;
            boolean keepReading = true;
 
            do {
                clientMessage = input.readUTF().trim();
                // Only commands start with a backslash
                if (clientMessage.startsWith("\\")) {
                   String[] splitMessage = clientMessage.split(" "); 
                   switch(server.identifyCommand(splitMessage[0])) {
                        case LOGOUT:
                            keepReading = false;
                            break;
                        case LIST_COMMANDS:
                            String help = "\\logout - Terminates the connection \n" + 
                            "\\change_nick newName - Changes your nickname \n" +
                            "\\change_group groupName - Switch to the named chat room \n" +
                            "\\help - List all available commands \n" + 
                            "\\message userName myMessage - message only the specified user \n" +
                            "\\list_users - Lists all users in your current group";
                            output.writeUTF(help);
                            break;
                        case UNKNOWN:
                            output.writeUTF("Command was not recognized");
                            break;
                        case MESSAGE:
                            if (splitMessage.length > 2) {
                                String message = "";
                                for(int i = 2; i < splitMessage.length; ++i) message += splitMessage[i] + " ";
                                if(server.message(splitMessage[1], this.userName + " whispered to you: " + message)) output.writeUTF("Whispered to: " + splitMessage[1]);
                                else output.writeUTF("ERROR: No such username found");
                            } else output.writeUTF("ERROR: No message to send");
                            break;
                        case CHANGE_GROUP:
                            if (splitMessage.length > 1) {
                                String message = "";
                                for(int i = 1; i < splitMessage.length; ++i) message += splitMessage[i] + " ";
                                this.server.publishMessage(this.userName + " left the group.", this, this.groupName);
                                this.groupName = message.trim();
                                this.server.publishMessage(this.userName + " entered the group.", this, this.groupName);
                            } 
                            else output.writeUTF("ERROR: No group specified");
                            break;
                        case CHANGE_NICK:
                            if (splitMessage.length > 1) {
                                String message = "";
                                for(int i = 1; i < splitMessage.length; ++i) message += splitMessage[i] + " ";
                                this.server.publishMessage(this.userName + " changed his nick to: " + message.trim(), this, this.groupName);
                                this.userName = message.trim();
                                output.writeUTF("Your new nick is: " + this.userName);
                            }
                            else output.writeUTF("ERROR: No nick specified");
                            break;
                        case LIST_USERS:
                            List<String> users = this.server.getUserNames(this.groupName);
                            String message = "Group " + this.groupName + " curently has: ";
                            for(String name : users) message += name + ", ";
                            message += "as active users";
                            output.writeUTF(message);
                            break;
                   }
                } else server.publishMessage("[" + this.userName + "]: " + clientMessage, this, this.groupName);
            } while (keepReading);
 
            server.removeUser(this);
            socket.close();
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            System.out.println("Connection with " + this.userName + " lost.");
            try {
              server.publishMessage("Connection with " + this.userName + " lost.", this, this.groupName);
                server.removeUser(this);  
            } catch(Exception e) {}; 
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
