package chat.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UdpListener implements Runnable {
    private final DatagramSocket socket;
    private final ConcurrentLinkedQueue<ClientData> clients;

    public UdpListener(DatagramSocket socket, ConcurrentLinkedQueue<ClientData> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        System.out.println("Start Udp Listener");

        byte[] receivedBuffer = new byte[4096];
        DatagramPacket receivedPacket = new DatagramPacket(receivedBuffer, receivedBuffer.length);

        while (true) {
            try {
                this.socket.receive(receivedPacket);
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (message.equals("[connect]")) {
                    this.clients.add(new ClientData(receivedPacket.getAddress(), receivedPacket.getPort()));
                } else {
                    ClientData sender = new ClientData(receivedPacket.getAddress(), receivedPacket.getPort());
                    System.out.println("Udp Message: \n" + message);

                    for (ClientData udpClient : this.clients) {
                        if (!(udpClient.getAddress().equals(sender.getAddress()) && udpClient.getPort() == sender.getPort())) {
                            byte[] sendBuffer = message.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, udpClient.getAddress(), udpClient.getPort());
                            socket.send(sendPacket);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}