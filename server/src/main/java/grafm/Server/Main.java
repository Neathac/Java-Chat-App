package grafm.Server;

import grafm.Server.Server;

public class Main {
    public static void main(String[] args) {
      Server server;
      if(args.length < 1) server = new Server();
      else server = new Server(args[0]);
      server.start();
    }
}
