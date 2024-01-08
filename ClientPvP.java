import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ClientPvP {

    private final String serverAddress;
    private final int serverPort;
    private final Model model;
    private final LevelLoader levelLoader;

    private AsynchronousSocketChannel clientChannel;

    public ClientPvP(String serverAddress, int serverPort, Model model) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.model = model;
        levelLoader = new LevelLoader(serverAddress, serverPort-1);
    }

    public void start() {
        try {
            clientChannel = AsynchronousSocketChannel.open();
            clientChannel.connect(new InetSocketAddress(serverAddress, serverPort), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    System.out.println("Connected to PvP server");
                    listenForServerMessages();
                }

                @Override
                public void failed(Throwable t, Void attachment) {
                    System.err.println("Failed to connect to PvP server: " + t.getMessage());
                    model.stopPvP();
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to open AsynchronousSocketChannel: " + e.getMessage());
            model.stopPvP();
        }
    }

    private void listenForServerMessages() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    stop();
                    return;
                }

                attachment.flip();

                String message = StandardCharsets.UTF_8.decode(attachment).toString().trim();

                if ("START".equalsIgnoreCase(message)) {
                    System.out.println("START");
                    model.startPvP();
                } else if ("LOSE".equalsIgnoreCase(message)) {
                    System.out.println("LOSE");
                    model.losePvP();
                }  else if ("WIN".equalsIgnoreCase(message)) {
                    System.out.println("WIN");
                    model.winPvP();
                }  else {
                    model.updateEnemy(message);
                }

                attachment.clear();
                if (clientChannel.isOpen()) {
                    clientChannel.read(buffer, buffer, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.err.println("Error receiving message: " + exc.getMessage());
            }
        });
    }    

    public void updateModel(Model model) {
        sendMessage(levelLoader.parseLevelToString(model));
    }

    public void imWin() {
        sendMessage("WIN");
    }

    private void sendMessage(String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                System.out.println("Send message: " + message);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                System.err.println("Error sending message: " + exc.getMessage());
            }
        });
    }

    public void stop() {
        try {
            if (clientChannel != null) {
                clientChannel.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

}
