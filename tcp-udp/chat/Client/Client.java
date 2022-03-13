package chat.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    final int MAX_CLIENTS = 5;

    private final int serverPort;

    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
    private Socket clientSocket;
    private DatagramSocket updSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;

    InetAddress serverAddress;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    private void initializeClient() {
        try {
            this.clientSocket = new Socket("localhost", this.serverPort);
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
            this.clientReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            System.out.println("Write your nick: ");

            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            clientWriter.println(name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tcpListen() {
        this.executorService.execute(() -> {
            while (true) {
                try {
                    String receivedMessage = this.clientReader.readLine();
                    System.out.println(receivedMessage);
                } catch (IOException e) {
                    System.out.println("Error while receiving message.");
                }
            }
        });
    }

    private void userInputListen() {
        Scanner scanner = new Scanner(System.in);
        String messageToSend;
        while (true) {
            messageToSend = scanner.nextLine();
            if (messageToSend.equals("U")) {
                String asciiArt = """
                        __________████████_____██████
                        _________█░░░░░░░░██_██░░░░░░█
                        ________█░░░░░░░░░░░█░░░░░░░░░█
                        _______█░░░░░░░███░░░█░░░░░░░░░█
                        _______█░░░░███░░░███░█░░░████░█
                        ______█░░░██░░░░░░░░███░██░░░░██
                        _____█░░░░░░░░░░░░░░░░░█░░░░░░░░███
                        ____█░░░░░░░░░░░░░██████░░░░░████░░█
                        ____█░░░░░░░░░█████░░░████░░██░░██░░█
                        ___██░░░░░░░███░░░░░░░░░░█░░░░░░░░███
                        __█░░░░░░░░░░░░░░█████████░░█████████
                        _█░░░░░░░░░░█████_████___████_█████___█
                        _█░░░░░░░░░░█______█_███__█_____███_█___█
                        █░░░░░░░░░░░░█___████_████____██_██████
                        ░░░░░░░░░░░░░█████████░░░████████░░░█
                        ░░░░░░░░░░░░░░░░█░░░░░█░░░░░░░░░░░░█
                        ░░░░░░░░░░░░░░░░░░░░██░░░░█░░░░░░██
                        ░░░░░░░░░░░░░░░░░░██░░░░░░░███████
                        ░░░░░░░░░░░░░░░░██░░░░░░░░░░█░░░░░█
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░█
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░█
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░█
                        ░░░░░░░░░░░█████████░░░░░░░░░░░░░░██
                        ░░░░░░░░░░█▒▒▒▒▒▒▒▒███████████████▒▒█
                        ░░░░░░░░░█▒▒███████▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒█
                        ░░░░░░░░░█▒▒▒▒▒▒▒▒▒█████████████████
                        ░░░░░░░░░░████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒█
                        ░░░░░░░░░░░░░░░░░░██████████████████
                        ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░█
                        ██░░░░░░░░░░░░░░░░░░░░░░░░░░░██
                        ▓██░░░░░░░░░░░░░░░░░░░░░░░░██
                        ▓▓▓███░░░░░░░░░░░░░░░░░░░░█
                        ▓▓▓▓▓▓███░░░░░░░░░░░░░░░██
                        ▓▓▓▓▓▓▓▓▓███████████████▓▓█
                        ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓██
                        ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓█
                        ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓█""";

                byte[] sendBuffer = asciiArt.getBytes();
                DatagramPacket sendPacket =
                        new DatagramPacket(sendBuffer, sendBuffer.length, this.serverAddress, this.serverPort);
                try {
                    this.updSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                this.clientWriter.println(messageToSend);
            }
        }
    }

    private void udpListen() {
        Runnable runnableTask = () -> {
            byte[] receivedBuffer = new byte[4096];
            while (true) {
                DatagramPacket receivePacket =
                        new DatagramPacket(receivedBuffer, receivedBuffer.length);
                try {
                    this.updSocket.receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("UDP: \n" + new String(receivePacket.getData(), 0, receivePacket.getLength()));
            }
        };
        this.executorService.execute(runnableTask);
    }

    private void initializeUdp() {
        try {
            this.updSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost");
            byte[] sendBuffer = "[connect]".getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendBuffer, sendBuffer.length, this.serverAddress, this.serverPort);
            try {
                this.updSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startClient() {
        this.initializeClient();
        this.initializeUdp();
        this.tcpListen();
        this.udpListen();
        this.userInputListen();
    }

    public void stopClient() {
        try {
            if (this.clientSocket != null) {
                this.clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}