import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerPvP {

    private AsynchronousServerSocketChannel serverChannel;
    private final ConcurrentLinkedQueue<AsynchronousSocketChannel> newClients = new ConcurrentLinkedQueue<>();

    public ServerPvP(int port) {
        try {
            serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
            System.out.println("PvP Server is listening on port " + port);
        } catch (IOException e) {
            System.err.println("PvP Server failed to start: " + e.getMessage());
        }
    }

    public void start() {
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                System.out.println("Client connected: " + clientChannel);
                serverChannel.accept(null, this);
                newClients.add(clientChannel);
                matchClients();
            }

            @Override
            public void failed(Throwable t, Void attachment) {
                System.err.println("Failed to accept a connection: " + t.getMessage());
            }
        });
    }

    private synchronized void matchClients() {
        if (newClients.size() < 2) {
            return; 
        }

        AsynchronousSocketChannel player1 = newClients.poll();
        AsynchronousSocketChannel player2 = newClients.poll();

        System.out.println("send START");
        sendMessage(player1, player2, "START");
        sendMessage(player2, player1, "START");

        startReading(player1, player2);
        startReading(player2, player1);
    }

    private void startReading(AsynchronousSocketChannel player, AsynchronousSocketChannel enemy) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        player.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    handleClientDisconnect(player, enemy);
                    return;
                }

                attachment.flip();
                String message = StandardCharsets.UTF_8.decode(attachment).toString().trim();

                if ("WIN".equalsIgnoreCase(message)) {
                    System.out.println("send LOSE");
                    sendMessage(enemy, player, "LOSE");
                } else if ("LOSE".equalsIgnoreCase(message)) {
                    System.out.println("send WIN");
                    sendMessage(enemy, player, "WIN");
                } else {
                    // updateModel
                    System.out.println("send UPDATE");
                    sendMessage(enemy, player, message);
                }

                attachment.clear();
                if (player.isOpen()) {
                    player.read(buffer, buffer, this); 
                }
            }

            @Override
            public void failed(Throwable t, ByteBuffer attachment) {
                System.err.println("Failed to read message from client " + t.getMessage());
                handleClientDisconnect(player, enemy);
            }
        });
    }

    private void sendMessage(AsynchronousSocketChannel player, AsynchronousSocketChannel enemy, String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        player.write(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                // Message sent
                System.out.println("SEND: [" + player + "] message: " + message);
            }

            @Override
            public void failed(Throwable t, Void attachment) {
                System.err.println("Failed to send message: " + t.getMessage());
                handleClientDisconnect(player, enemy);
            }
        });
    }

    /**
     * One of the clients has disconnected, 
     * we notify the other of the victory and close both sockets
     * @param player
     * @param enemy
     */
    private void handleClientDisconnect(AsynchronousSocketChannel player, AsynchronousSocketChannel enemy) {
        if (enemy.isOpen()) {
            // Your opponent has disconnected. You win by default.
            System.out.println("handleClientDisconnect");
            sendMessage(enemy, player, "WIN");
            closeClientConnection(enemy);
        }
        closeClientConnection(player);
    }

    private void closeClientConnection(AsynchronousSocketChannel clientChannel) {
        try {
            clientChannel.close();
            System.out.println("Client win and disconnect: " + clientChannel);
        } catch (IOException e) {
            System.err.println("Error closing client channel: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        int SERVER_PORT = 7777;        
        int PVP_SERVER_PORT = 7778;
        int CLIENT_POOL_SIZE = 10;

        Server server = new Server(SERVER_PORT, CLIENT_POOL_SIZE);
        server.start();

        ServerPvP serverPvP = new ServerPvP(PVP_SERVER_PORT);
        serverPvP.start();

        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Enter 'exit' to stop the Sokoban Server.");
        while (true) {
            input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.exit(0);
            }
        }
    }
}
