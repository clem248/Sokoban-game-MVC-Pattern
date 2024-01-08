import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Server extends Thread {

    private final int port;
    private final int poolSize;
    private final LevelLoader levelLoader;

    public Server(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
        this.levelLoader = new LevelLoader(null, 0);
    }

    /**
     * Server thread
     */
    @Override
    public void run() {
        ExecutorService clients = Executors.newFixedThreadPool(poolSize);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (!this.isInterrupted()) {
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket, levelLoader);
                clients.execute(client);
            } 
        } catch (IOException ioe) {
            System.err.println(ioe);
        }

        clients.shutdown();
        System.out.println("Server stopped");
    }
    
    public void close() {
        Thread.currentThread().interrupt();
    }
}


class Client implements Runnable {

    private final Socket clientSocket;
    private final LevelLoader levelLoader;

    public Client(Socket clientSocket, LevelLoader levelLoader) {
        this.clientSocket = clientSocket;
        this.levelLoader = levelLoader;

        System.out.println(getClientInfo() + " Client connected.");
    }

    /**
     * Client thread
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            int level = parseLevel(in.readLine());
            out.println(loadLevel(level)); 
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
            close();
        }
    }

    private int parseLevel(String level) {
        try {
            return Integer.parseInt(level);
        } catch (NumberFormatException nfe) {
            System.err.println(getClientInfo() + " Error parsing level number: " + level);
            return -1;
        }
    }

    private String loadLevel(int level) {
        if (7 <= level && level <= 9) {
            System.out.println(getClientInfo() + " Load level number: " + level);
            return levelLoader.serverLoad(level);
        }
        if (level == -1) {
            return "";

        }
        System.err.println(getClientInfo() + " Invalid level number: " + level);
        return "";
    }

    private void close() {
        try {
            System.out.println(getClientInfo() + " Client disconnected.");
            clientSocket.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private String getClientInfo() {
        InetAddress addr = clientSocket.getInetAddress();
        int port = clientSocket.getPort();
        return "[" + addr.getHostAddress() + ":" + port + "]";
    }

}