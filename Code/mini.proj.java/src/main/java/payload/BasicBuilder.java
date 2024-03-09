package payload;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicBuilder {
    private static final Logger logger = Logger.getLogger(BasicBuilder.class.getName());

    public BasicBuilder() {
    }

    public String encode(Message msg) {
        var sb = new StringBuilder();
        sb.append(msg.getGroup()).append(",").append(msg.getName()).append(",").append(msg.getText());

        return sb.toString();
    }

    public Message decode(byte[] raw) {
        if (raw == null || raw.length == 0) {
            return null;
        }

        try {
            var s = new String(raw);
            var parts = s.split(",", 3);
            if (parts.length < 3) {
                logger.log(Level.WARNING, "Received malformed message: " + s);
                return null;
            }
            var rtn = new Message(parts[1], parts[0], parts[2]);

            return rtn;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while decoding message", e);
            return null;
        }
    }
}