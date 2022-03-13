package chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {
    final int MAX_CLIENTS = 5;

    private final int portNumber;
    private ServerSocket serverSocket;
    private ConcurrentLinkedQueue<PrintWriter> clientWriters = new ConcurrentLinkedQueue<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void startServer() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("Error while creating server.");
            e.printStackTrace();
        }
        while(true) {
            Socket clientSocket = this.serverSocket.accept();
            PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientWriters.add(clientWriter);
            String clientName = clientReader.readLine();
            System.out.println("new client: " + clientName);

            executorService.execute(new TcpListener(clientReader, clientWriter, this.clientWriters, clientName));
        }

    }

    public void stopServer() {
        if(this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
