package grafm.Server;

import java.io.*;
import java.net.*;
import java.util.*;

import grafm.Server.ClientHandler;

public class Server {

  public enum commands {
    LOGOUT,
    CHANGE_NICK,
    LIST_COMMANDS,
    CHANGE_GROUP,
    MESSAGE,
    UNKNOWN,
  }

  private Set<ClientHandler> handlers = new HashSet<>();
  private final int PORT = 8089;

  public void start() {
    try {

      ServerSocket server = new ServerSocket(PORT);
      System.out.println("Listening on: " + PORT);

      while (true) {
        Socket socket = server.accept();
        System.out.println("New connection estabilished");

        // Runnable is better suited to providing us its data on the fly
        ClientHandler user = new ClientHandler(this, socket);
        Thread newThread = new Thread(user);
        handlers.add(user);
        newThread.start();
      }

    } catch (IOException e) {
      System.out.println(e);
    }
  }

  void publishMessage(String message, ClientHandler excludeUser) throws IOException {
    for (ClientHandler aUser : handlers) {
      if (aUser != excludeUser) {
        aUser.output.writeUTF(message);
      }
    }
  }

  void removeUser(ClientHandler user) {
    boolean removed = handlers.remove(user);
    if (removed) {
      System.out.println(user.userName + " left");
    }
  }

  List<String> getUserNames(String groupName) {
    List<String> userNames = new Vector<String>();
    for (ClientHandler handler : handlers)
      if (handler.groupName == groupName)
        userNames.add(handler.userName);
    return userNames;
  }

  /**
   * Returns true if there are other users connected (not count the currently
   * connected user)
   */
  boolean hasUsers(String groupName) {
    for (ClientHandler handler : handlers)
      if (handler.groupName == groupName)
        return true;
    return false;
  }

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
      default:
        return commands.UNKNOWN;
    }
  }

  public boolean message(String target, String message) {
    boolean sent = false;
    for (ClientHandler handler : handlers) if (handler.userName == target) { 
      handler.output.writeUTF(message);
      sent = true;
    }
    return sent;
  }

}