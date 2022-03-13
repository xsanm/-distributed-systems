package chat.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpListener implements Runnable {

    private final ConcurrentLinkedQueue<PrintWriter> clientWriters;
    private final BufferedReader clientReader;
    private final PrintWriter clientWriter;

    private final String clientNick;

    public TcpListener(BufferedReader clientReader, PrintWriter clientWriter, ConcurrentLinkedQueue<PrintWriter> clientWriters, String clientNick) {
        this.clientNick = clientNick;
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
                System.out.println("Tcp message: " + newMessage);

                for (PrintWriter userWriter : this.clientWriters) {
                    if (userWriter != this.clientWriter) {
                        userWriter.println(this.clientNick + ": " + newMessage);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while sending data to clients.");
                e.printStackTrace();
            }
        }
    }
}
