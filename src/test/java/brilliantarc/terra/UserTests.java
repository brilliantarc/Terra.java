package brilliantarc.terra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import static brilliantarc.terra.DefaultSettings.*;

/**
 * Test cases around user accounts.
 */
public class UserTests {

    private Log log = LogFactory.getLog("brilliantarc.terra.test");

    @Test
    public void authentication() {
        Client.Services services = client.authenticate("admin", "admin!");
        assertNotNull(services.getUser());
        assertNotNull(services.getUser().getCredentials());
    }

    @Test
    public void authenticationFailure() {
        try {
            client.authenticate("admin", "bad password");
            fail("User authenticated with a bad password.");
        } catch (SingularityException e) {
            assertEquals("Server should have returned a 401 Authorized error.", 401, e.getStatus());
        }
    }

}
