package socket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicServerClientTest {

    String host = "127.0.0.1";
    int port = 2000;

    @Test
    public void testBasicServerClient() {
        try {
            // Start server in a separate thread
            BasicServer server = new BasicServer(host, port);
            new Thread(() -> server.start()).start();
            
            // Give server some time to start
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Create client and connect to server
            BasicClient client = new BasicClient("test",host, port);
            client.connect();
            
            // Send message and verify response
            String response = client.sendMessage("Test message");
            assertEquals("Acknowledged", response);
            
            // Stop server
            server.stop();
        } catch (IOException ex) {
            Logger.getLogger(BasicServerClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
