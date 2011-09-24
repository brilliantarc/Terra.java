package brilliantarc.terra;

import org.junit.Test;
import static org.junit.Assert.*;
import static brilliantarc.terra.DefaultSettings.*;

/**
 * Check out some general purpose functionality, like slugs and UUIDs.
 */
public class BasicTests {

    @Test
    public void generateSlug() {
        String slug = terra.slugify("Som‘ STRaNGE V‡lue");
        assertNotNull(slug);
        assertEquals("some-strange-value", slug);
    }

    @Test
    public void generateUUID() {
        String uuid = terra.uuid();
        assertNotNull(uuid);
        assertTrue(uuid.matches("[A-Za-z0-9\\-]+"));
    }
}
