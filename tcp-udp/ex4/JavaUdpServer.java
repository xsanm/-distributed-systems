package ex4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String[] args) {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try {
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(datagramPacket);
                String msg = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("received msg: " + msg + " " + datagramPacket.getAddress());

                String response = "";
                if(msg.toLowerCase().contains("python")){
                    response = "Pong Python";
                } else if(msg.toLowerCase().contains("java")){
                    response = "Pong Java";
                }

                System.out.println("send msg: " + response);
                socket.send(new DatagramPacket(response.getBytes(), response.getBytes().length, datagramPacket.getAddress(), datagramPacket.getPort()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
