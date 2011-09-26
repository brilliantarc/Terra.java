package brilliantarc.terra;

import brilliantarc.terra.server.Request;
import brilliantarc.terra.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Use this class to interact with the Terra server API.  Based on the Jersey
 * REST client libraries.
 *
 * The Terra client library tries to remain semi-functional, in that no call
 * modifies existing object (though you are free to do so).  For example, when
 * you update a category, a new Category object is returned with the updated
 * information; the original Category object you submitted is not modified.
 * Additionally, there are no interactions or server commands performed on the
 * brilliantarc.terra.node classes at all.  These are simple POJOs that are
 * filled by the JSON parser based on results from the server.  While you may
 * be required to modify them and pass them back to Terra for updates, the
 * service calls will not alter those classes upon their return.
 *
 * Calls to the library also tend to return small amounts of data, preferring
 * many quick calls to fewer, large, all-encompassing calls.  For example, when
 * you request a taxonomy, the synonyms and top-level categories are not returned;
 * those require a separate call.  We presume you won't necessarily need the
 * synonyms all the time, so the extra calls and data required to always send
 * that information is extraneous in 80% of the cases.  (The Terra Client UI
 * has also been redesigned around these principles.)
 *
 * To make a request of the server, first authenticate a user against an
 * instance of the Terra.Client, then submit requests across that client's
 * services.
 *
 * <verbatim>
 *   Client.Services terra = client.authenticate("someuser", "somepassword");
 *   Taxonomy taxonomy = terra.taxonomies().create("FON", "Sample Name", "sample-slug", "en");
 *   Taxonomy check = terra.taxonomies().get("FON", "sample-slug");
 * </verbatim>
 *
 * You'll find more examples of using Terra in the test cases with the source
 * code.
 */
public class Client {

    private String baseUrl;
    private com.sun.jersey.api.client.Client jerseyClient;

    private static Log log = LogFactory.getLog("brilliantarc.terra");

    /**
     * Hold on to a user object and issue requests with it.
     */
    public static class Services {
        private Client client;
        private User user;

        Services(Client client, User user) {
            this.client = client;
            this.user = user;
        }

        /**
         * @return a reference to the Terra client servicing this object
         */
        public Client getClient() {
            return client;
        }

        /**
         * @return a reference to the authenticated user account associated with this service
         */
        public User getUser() {
            return user;
        }

        public Request request() {
            return new Request(client.jerseyClient, client.baseUrl, user);
        }

        /**
         * Convert the given value into a Terra-compatible, SEO string.  Basically
         * lowercases the string, cleans out or transcodes special characters, and
         * replaces any spaces with dashes.  Useful in URLs.
         *
         * Performed by the Terra server, so the string you pass in will be the same
         * one that would be generated from a name should you send in a null value
         * for a slug.
         *
         * @param value the value to "slugify"
         *
         * @return the newly created slug
         */
        public String slugify(String value) {
            return request().to("slug").send("value", value).toString();
        }

        /**
         * Generate a universally unique identifier (UUID, aka GUID) on the
         * Terra server.
         *
         * @return the UUID
         */
        public String uuid() {
            return request().to("uuid").send().toString();
        }

        /**
         * Used in testing to reset the test operating company in the Terra
         * server.
         */
        public void reset() {
            log.debug(request().to("test/reset").send().toString());
        }

        /**
         * @return operating company-related services
         */
        public OperatingCompanies operatingCompanies() {
            return new OperatingCompanies(this);
        }

        /**
         * @return taxonomy-related services
         */
        public Taxonomies taxonomies() {
            return new Taxonomies(this);
        }

        /**
         * @return category-related services
         */
        public Categories categories() {
            return new Categories(this);
        }

        /**
         * @return property-related services
         */
        public Properties properties() {
            return new Properties(this);
        }

        /**
         * @return option-related services
         */
        public Options options() {
            return new Options(this);
        }

        /**
         * @return heading-related services
         */
        public Headings headings() {
            return new Headings(this);
        }

        /**
         * @return superheading-related services
         */
        public Superheadings superheadings() {
            return new Superheadings(this);
        }

        /**
         * @return synonym-related services
         */
        public Synonyms synonyms() {
            return new Synonyms(this);
        }

        /**
         * @return user-related services
         */
        public Users users() {
            return new Users(this);
        }

        public Search search() {
            return new Search(this);
        }
    }

    /**
     * Create a new instance of a Terra client.  Clients are based on the Jersey
     * Client library, and are thread-safe.  We recommend you create a single
     * instance of Client for your entire application, as the creation of
     * Client instances is expensive.
     *
     * @param terraUrl              the URL to the Terra API server, e.g. "http://terra.eurodir.eu/api"
     * @param connectionTimeout     the connection timeout (how long to wait to see if the server is there), in milliseconds
     * @param readTimeout           the read timeout (how long to wait for an answer), in milliseconds
     */
    public Client(String terraUrl, Integer connectionTimeout, Integer readTimeout) {
        baseUrl = terraUrl;

        jerseyClient = new com.sun.jersey.api.client.Client();
        jerseyClient.setConnectTimeout(connectionTimeout);
        jerseyClient.setReadTimeout(readTimeout);
        jerseyClient.setFollowRedirects(true);
    }

    /**
     * Create a new client connection to Terra using the default connection
     * timeout of 60 seconds, and the read timeout of fifteen minutes.
     *
     * @param terraUrl              the URL to the Terra API server, e.g. "http://terra.eurodir.eu/api"
     */
    public Client(String terraUrl) {
        this(terraUrl, 60000, 900000);
    }

    /**
     * Attempt to authenticate a user.  Returns the user account, with which
     * you use make subsequent API requests.
     *
     * The Services object returned by this method is thread-safe.
     *
     * @param login         the user's account login
     * @param password      the user's account password
     *
     * @return  an authenticated Services object to use to make requests to the Terra server
     * @throws  SingularityException if the account is invalid or authentication
     *          fails
     */
    public Services authenticate(String login, String password) {
        User user = request().to("user_session").post().send("login", login, "password", password).as(User.class);
        return new Services(this, user);
    }

    /**
     * Used internally.  Use the authenticate() method instead.
     *
     * @return  a Terra request object, based on the configured client and Terra API URL
     */
    public Request request() {
        return new Request(jerseyClient, baseUrl);
    }
}
