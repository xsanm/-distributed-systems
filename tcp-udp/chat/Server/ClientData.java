package chat.Server;

import java.net.InetAddress;

public record ClientData(InetAddress address, int port) {

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
