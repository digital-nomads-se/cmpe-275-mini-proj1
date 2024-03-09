package socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import payload.BasicBuilder;
import payload.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

class SessionHandler extends Thread {

    private Socket connection;
    private boolean forever = true;
    private static final Logger logger = Logger.getLogger(SessionHandler.class.getName());

    public SessionHandler(Socket connection) {
        this.connection = connection;

        // allow server to exit if
        this.setDaemon(true);
    }

    /**
     * stops session on next timeout cycle
     */
    public void stopSession() {
        forever = false;
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An error occurred while stopping the session", e);
            }
        }
        connection = null;
    }

    /**
     * process incoming data
     */
    @Override
    public void run() {
        System.out.println("Session " + this.getId() + " started");

        try {
            connection.setSoTimeout(2000);
            var in = new BufferedInputStream(connection.getInputStream());

            byte[] raw = new byte[2048];
            BasicBuilder builder = new BasicBuilder();
            while (forever) {
                try {
                    int len = in.read(raw);
                    if (len == 0) {
                        continue;
                    } else if (len == -1) {
                        break;
                    }

                    Message msg = builder.decode(new String(raw, 0, len).getBytes());
                    System.out.println(msg);

                } catch (InterruptedIOException ioe) {
                    logger.log(Level.WARNING, "Interrupted I/O Exception", ioe);
                    break;
                }
            }
        } catch (IOException | RuntimeException e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        } finally {
            try {
                logger.log(Level.INFO, "Session {0} ending", this.getId());
                stopSession();
            } catch (Exception re) {
                logger.log(Level.SEVERE, "An error occurred while stopping the session", re);
            }
        }
    }

} 
