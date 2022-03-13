package chat.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    final int MAX_CLIENTS = 5;

    private final int serverPort;

    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
    private Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;

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
                    String recievedMessage = this.clientReader.readLine();
                    System.out.println(recievedMessage);
                } catch (IOException e) {
                    System.out.println("Error while receiving message.");
                }
            }
        });
    }

    private void userInputListen() {
        Scanner scanner = new Scanner(System.in);

            while (true) {
                String messageToSend = scanner.nextLine();
                this.clientWriter.println(messageToSend);

            }


    }

    public void startClient() {
        this.initializeClient();
        this.tcpListen();
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