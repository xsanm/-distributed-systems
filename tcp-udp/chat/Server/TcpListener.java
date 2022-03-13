package chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpListener implements Runnable {

    private final ConcurrentLinkedQueue<PrintWriter> clientWriters;
    private final BufferedReader clientReader;
    private final PrintWriter clientWriter;

    private String clientName;

    public TcpListener(BufferedReader clientReader, PrintWriter clientWriter, ConcurrentLinkedQueue<PrintWriter> clientWriters, String clientName) {
        this.clientName = clientName;
        this.clientReader = clientReader;
        this.clientWriter = clientWriter;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        String newMessage;
        while (true) {
            try {
                newMessage = clientReader.readLine();
                System.out.println("Server get message: " + newMessage);

                for (PrintWriter userWriter : this.clientWriters) {
                    if (userWriter != this.clientWriter) {
                        userWriter.println(this.clientName + ": " + newMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
