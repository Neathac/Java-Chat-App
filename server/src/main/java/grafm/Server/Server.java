package grafm.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import grafm.Server.ClientHandler;
import java.util.concurrent.Executors;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.ThreadPoolExecutor;  

/**
* A multithreaded class managing connections and distributing messages
* It keeps track of connected clients using ClientHandler Runnable classes
*/
public class Server {

  /**
   * Enum of possible clientside commands to the server
   */
  public enum commands {
    LOGOUT,
    CHANGE_NICK,
    LIST_COMMANDS,
    CHANGE_GROUP,
    MESSAGE,
    LIST_USERS,
    UNKNOWN,
  }

  private int PORT = 8089;

  /**
   * Default constructor
   * Assigns the port on which to listen to be 8089
   */
  public Server() {
    PORT = 8089;
  }

  /**
   * Constructor class
   * @param port - Specification on which port to listen for requests
   */
  public Server(String port) {
    PORT = Integer.parseInt(port);
  }

  private Set<ClientHandler> handlers = new HashSet<>();
  ExecutorService executor;

  /**
   * Initiate the server listening in an infinite loop on the socket specified via constructor
   * Uses an Executor to manage 7 threads running ClientHandler classes
   * One ClientHandler class represents one connected client
   */
  public void start() {
    try {

      ServerSocket server = new ServerSocket(PORT);
      System.out.println("Listening on: " + PORT);
      executor = Executors.newFixedThreadPool(7);

      while (true) {
        // Accept incoming requests
        Socket socket = server.accept();
        System.out.println("New connection estabilished");

        // We keep track of the new client
        ClientHandler user = new ClientHandler(this, socket);
        executor.execute(user);
        handlers.add(user);
      }

      // Server was interrupted via console
    } catch (IOException e) {
      executor.shutdown();
    }
  }

  /**
   * Broadcast the message to other users in a group
   * @param message - WHat to publish in the group
   * @param excludeUser - Used to not send the message back to the sender
   * @param groupName - Which group to publish the message in
   * @throws IOException - Client may be already closed from clientside
   */
  void publishMessage(String message, ClientHandler excludeUser, String groupName) throws IOException {
    for (ClientHandler aUser : handlers) {
      if (aUser != excludeUser && aUser.groupName.equals(groupName)) {
        aUser.output.writeUTF(message);
      }
    }
  }
  
  /**
   * Removes the user from the active pool of user connections
   * @param user - ClientHandler instance of the user to remove
   */
  void removeUser(ClientHandler user) {
    boolean removed = handlers.remove(user);
    if (removed) {
      System.out.println(user.userName + " left");
    }
  }

  /**
   * Finds all users in the specified group
   * @param groupName - Name of group which to look through
   * @return - List of usernames currently active in the group
   */
  public List<String> getUserNames(String groupName) {
    List<String> userNames = new Vector<String>();
    for (ClientHandler handler : handlers)
      if (handler.groupName.equals(groupName))
        userNames.add(handler.userName);
    return userNames;
  }

  /**
   * Checks if group has active users
   * @return - Returns true if there are other users connected (not count the currently
   * connected user)
   */
  boolean hasUsers(String groupName) {
    for (ClientHandler handler : handlers)
      if (handler.groupName == groupName)
        return true;
    return false;
  }

  /**
   * Checks the recieved command against the list of known commands
   * @param entry - Client entered command
   * @return - cammand enum values mapped to the entry
   */
  public commands identifyCommand(String entry) {
    switch(entry.toLowerCase().trim()) {
      case "\\logout":
        return commands.LOGOUT;
      case "\\change_group":
        return commands.CHANGE_GROUP;
      case "\\change_nick":
        return commands.CHANGE_NICK;
      case "\\help":
        return commands.LIST_COMMANDS;
      case "\\message":
        return commands.MESSAGE;
      case "\\list_users":
        return commands.LIST_USERS;
      default:
        return commands.UNKNOWN;
    }
  }

  /**
   * Sends a message to the specified user
   * @param target - Message recipient name
   * @param message - Message contents
   * @return - boolean indicator of whether the message was sent successfully
   */
  public boolean message(String target, String message) {
    boolean sent = false;
    for (ClientHandler handler : handlers) {
      if (handler.userName.equals(target)) { 
        try {
          handler.output.writeUTF(message);
          sent = true;
        } catch (IOException e) {
          System.out.println("Error writing to server: " + e.getMessage());
        }
      } 
    }
    return sent;
  }

}