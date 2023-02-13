package grafm.Client;

import grafm.Client.Client;

public class Main {
    public static void main(String[] args) {
        //if (args.length < 2) return;
 
        Client client = new Client();
        client.connect();
    }
}
