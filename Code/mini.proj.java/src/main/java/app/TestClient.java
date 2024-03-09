package app;

import socket.BasicClient;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestClient implements Runnable {

    private final int numRequests;
    private static final Logger logger = Logger.getLogger(TestClient.class.getName());

    public TestClient(int numRequests) {
        this.numRequests = numRequests;
    }

    @Override
    public void run() {
        try {
            BasicClient client = new BasicClient("app", "127.0.0.1", 2000);
            client.connect();
            client.join("group_chat");
            for (int i = 0; i < numRequests; i++) {
                String message = "This is a test message " + i;
                client.sendMessage(message);
            }
            client.stop();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred in TestClient", e);
        }
    }

    public static void main(String[] args) {
        int numClients = 10000;
        int numRequests = 100;

        for (int i = 0; i < numClients; i++) {
            new Thread(new TestClient(numRequests)).start();
        }
    }
}
