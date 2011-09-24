package brilliantarc.terra;

/**
 * Default settings used in (almost) all test cases.
 */
public class DefaultSettings {

    public static Client client = new Client("http://localhost:4000");

    /**
     * Whatever you do, DO NOT USE THIS as an example of how to manage connections
     * in your own applications.  Each user should authenticate with his or her
     * own credentials, otherwise tracking and history in Terra will be useless
     * for any changes made using your client application.
     */
    public static Client.Services terra = client.authenticate("admin", "admin!");

}
