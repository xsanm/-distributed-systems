package chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    final int MAX_CLIENTS = 5;

    private final int portNumber;
    private ServerSocket serverSocket;
    private final ConcurrentLinkedQueue<PrintWriter> clientWriters = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS + 1);
    private final ConcurrentLinkedQueue<ClientData> updClients = new ConcurrentLinkedQueue<>();

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }


    private void startTcp() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("Error while creating tcp listener.");
            e.printStackTrace();
        }
        System.out.println("Start Tcp listener");

        while (true) {
            Socket clientSocket = this.serverSocket.accept();
            PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientWriters.add(clientWriter);
            String clientNick = clientReader.readLine();
            System.out.println("new client: " + clientNick);

            executorService.execute(new TcpListener(clientReader, clientWriter, this.clientWriters, clientNick));
        }
    }

    private void startUdp() {
        try {
            DatagramSocket udpServerSocket = new DatagramSocket(this.portNumber);
            this.executorService.execute(new UdpListener(udpServerSocket, this.updClients));
        } catch (SocketException e) {
            System.out.println("Error while creating udp listener.");
            e.printStackTrace();
        }
    }

    public void startServer() throws IOException {
        this.startUdp();
        this.startTcp();
    }

    public void stopServer() {
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
