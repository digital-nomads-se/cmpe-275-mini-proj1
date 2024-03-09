package payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BasicBuilderTest {
    @Test
    public void testEncode() {
        BasicBuilder builder = new BasicBuilder();
        Message msg = new Message("name1", "group1", "text1");
        String encoded = builder.encode(msg);
        assertEquals("group1,name1,text1", encoded);
    }

    @Test
    public void testDecode() {
        BasicBuilder builder = new BasicBuilder();
        String raw = "group1,name1,text1";
        Message decoded = builder.decode(raw.getBytes());
        assertEquals("group1", decoded.getGroup());
        assertEquals("name1", decoded.getName());
        assertEquals("text1", decoded.getText());
    }

    @Test
    public void testDecodeWithMalformedMessage() {
        BasicBuilder builder = new BasicBuilder();
        String raw = "group1,name1";
        Message decoded = builder.decode(raw.getBytes());
        assertNull(decoded);
    }

    @Test
    public void testDecodeWithNull() {
        BasicBuilder builder = new BasicBuilder();
        Message decoded = builder.decode(null);
        assertNull(decoded);
    }
}