package grafm.Client;

import grafm.Client.Client;

public class Main {
    public static void main(String[] args) {
        Client client;
        if (args.length < 2) client = new Client();
        else client = new Client(args[0], args[1]);
        
        client.connect();
    }
}
