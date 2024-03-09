package socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicServer {

    private long requestCount = 0;
    private long totalDuration = 0;
    private final Selector selector;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private volatile boolean shutdown = false;
    private static final Logger logger = Logger.getLogger(BasicServer.class.getName());

    public BasicServer(String hostname, int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(hostname, port));
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() {
        try {
            while (!shutdown) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    try {
                        if (key.isAcceptable()) {
                            register(selector, ((ServerSocketChannel) key.channel()));
                        }

                        if (key.isReadable()) {
                            answerWithEcho(buffer, key);
                        }
                    } catch (NotYetBoundException e) {
                        logger.log(Level.SEVERE, "ServerSocketChannel not yet bound", e);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "IO error", e);
                    }
                    iter.remove();
                }
            }
        } catch (ClosedSelectorException e) {
            // Selector has been closed, stop the server
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while starting the server", e);
        }
    }

    private void register(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
        long startTime = System.nanoTime();

        SocketChannel client = (SocketChannel) key.channel();
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();
        String receivedData = new String(buffer.array(), 0, bytesRead).trim();
        System.out.println("Received data from client: " + receivedData);

        // Send acknowledgement
        String acknowledgement = "Acknowledged";
        buffer.clear();
        buffer.put(acknowledgement.getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            client.write(buffer);
        }

        buffer.clear();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        totalDuration += duration;
        // logger.log(Level.INFO, "Processed request in {0} ns", duration);

        requestCount++;
        if (requestCount % 1000 == 0) {
            logger.log(Level.INFO, "Processed {0} requests", requestCount);
            logger.log(Level.INFO, "Total duration time: {0} ns", totalDuration);
            logger.log(Level.INFO, "Average request processing time: {0} ns", totalDuration / requestCount);
        }
    }

    public void stop() {
        shutdown = true;
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while stopping the server", e);
        }
    }
}
