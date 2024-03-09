package socket;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import payload.BasicBuilder;
import payload.Message;

/**
 *
 * @author utk
 */
public class BasicClient {

    private Socket clt;
    private String ipaddr;
    private int port;
    private String name;
    private String group;
    private static final Logger logger = Logger.getLogger(BasicClient.class.getName());

    public BasicClient(String name) {
        this(name, "127.0.0.1", 2000);
    }

    public BasicClient(String name, String ipaddr, int port) {
        this.name = name;
        this.ipaddr = ipaddr;
        this.port = port;
    }

    public void stop() {
        if (this.clt != null) {
            try {
                this.clt.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while closing the client", e);
            }
        }
        this.clt = null;
    }

    public void join(String group) {
        this.group = group;
    }

    public void connect() {
        if (this.clt != null && !this.clt.isClosed()) {
            return;
        }
    
        try {
            this.clt = new Socket(this.ipaddr, this.port);
            logger.log(Level.INFO, "Connected to {0}", clt.getInetAddress().getHostAddress());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while connecting", e);
        }
    }
    
    public String sendMessage(String message) {
        if (this.clt == null) {
            logger.log(Level.WARNING, "No connection, text not sent");
            return null;
        }

        try {
            BasicBuilder builder = new BasicBuilder();
            byte[] msg = builder.encode(new Message(name, group, message + "\n")).getBytes(StandardCharsets.UTF_8);
            this.clt.getOutputStream().write(msg);

            // Wait for acknowledgement
            byte[] buffer = new byte[1024];
            int bytesRead = this.clt.getInputStream().read(buffer);
            String acknowledgement = new String(buffer, 0, bytesRead);
            System.out.println("Received acknowledgement from server: " + acknowledgement);
            return acknowledgement;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while sending message", e);
            try {
                this.clt.close();
            } catch (IOException closeException) {
                logger.log(Level.SEVERE, "An error occurred while closing the socket", closeException);
            }
        }
        return null;
    }
}
