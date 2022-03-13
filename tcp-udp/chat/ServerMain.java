package chat;

import chat.Server.Server;

public class ServerMain {
    private final static int SERVER_PORT  = 7001;

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT);
        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client Connection Error");
        } finally {
            server.stopServer();
        }
    }
}
