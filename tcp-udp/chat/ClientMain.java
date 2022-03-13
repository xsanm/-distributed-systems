package chat;

import chat.Client.Client;

public class ClientMain {
    private final static int SERVER_PORT  = 7001;

    public static void main(String[] args) {
        Client client = new Client(SERVER_PORT);
        try {
            client.startClient();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client problem");
        } finally {
            client.stopClient();
        }
    }
}
