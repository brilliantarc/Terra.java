package brilliantarc.terra;

import brilliantarc.terra.node.Meme;
import brilliantarc.terra.server.Request;
import com.google.common.base.Function;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Errors generated by the server will throw a SingularityException with the
 * HTTP status, the error message from the server, and possibly a duplicate
 * meme, for those times when you try to create or update an object that
 * already exists.
 *
 * Note that every call to the Terra server will raise a SingularityException
 * on error.
 *
 * SingularityExceptions are RuntimeExceptions because I hate managing exceptions
 * in the middle and lower tiers.  Just let the exception bubble up to the top
 * of your application and handle it in the UI layer.
 */
public class SingularityException extends RuntimeException {

    private int status;
    private Meme duplicate;

    /**
     * Used to parse the JSON response from Terra when an error occurs.  You
     * do not need to reference this anywhere; it will be converted into a
     * SingularityException.
     */
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class SingularityError {
        private String error;
        private Meme duplicate;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Meme getDuplicate() {
            return duplicate;
        }

        public void setDuplicate(Meme duplicate) {
            this.duplicate = duplicate;
        }

        public static SingularityError fromJson(JsonNode node) {
            SingularityError error = new SingularityError();
            error.setError(node.path("error").getTextValue());

            if (node.has("duplicate")) {
                JsonNode duplicateNode = node.path("duplicate");
                Function<JsonNode, Meme> transformer = Request.JSON_FUNCTIONS.get(duplicateNode.path("definition").getTextValue());
                if (transformer != null) {
                    error.setDuplicate(transformer.apply(duplicateNode));
                }
            }

            return error;
        }
    }

    public SingularityException(int status, String s) {
        super(s);
        this.status = status;
    }

    public SingularityException(int status, String s, Throwable throwable) {
        super(s, throwable);
        this.status = status;
        this.duplicate = duplicate;
    }

    public SingularityException(int status, String s, Meme duplicate) {
        super(s);
        this.status = status;
        this.duplicate = duplicate;
    }

    /**
     * Used internally to generate the exception from a request to the Terra
     * API server.
     *
     * @param status    the HTTP status returned by Terra
     * @param error     the parse SingularityError JSON object
     */
    public SingularityException(int status, SingularityError error) {
        this(status, error.getError(), error.getDuplicate());
    }

    /**
     * The HTTP status code returned by the Terra server.  Terra uses the
     * HTTP status codes to indicate the error state that generated the
     * problem.  You can use these error codes to interpret the problem
     * and respond appropriately.
     *
     * It should be noted that the error messages returned from Terra are
     * designed to be "human-friendly", and in many cases may simply be
     * displayed directly to the end user.  For those cases where you need
     * to take additional action, checking the Status can be helpful.
     *
     * Possible status codes returned from Terra:
     *
     *   |  *Status*  | *Terra* | *HTTP* | *Reason* |
     *   |  404  | Not Found | Not Found | the requested information does not exist  |
     *   |  409  | Duplicate | Conflict | tried to create something with a slug or name that already exists  |
     *   |  424  | Missing Required Information | Failed Dependency | a required value was not submitted with the request  |
     *   |  406  | Invalid Parameter | Not Acceptable | tried to submit an invalid value  |
     *   |  406  | Invalid Relation | Not Acceptable | tried to relate two memes together with an invalid relation (verb)  |
     *   |  423  | In Use | Locked | tried to delete a piece of information that is still required by the system  |
     *   |  412  | Version Mismatch | Precondition Failed | tried to update or remove a node that was stale (on the client)  |
     *   |  401  | Authentication Failed | Unauthorized | the given user does not exist, or the password is wrong  |
     *   |  404  | User Not Found | Not Found | the given user account does not exist  |
     *   |  403  | Authorization Failed | Forbidden | the current user does not have permission to perform the action  |
     *   |  501  | Not Implemented | Not Implemented | requested a resource that is a future feature  |
     *
     * Some of the HTTP status codes are reused in different scenarios.
     * These scenarios are distinct, and should not cause confusing or
     * conflicting results.  For example, you won't get a 404 User Not Found
     * error when looking for a meme; it is solely for when you are looking up
     * another user account for information, such as an email address.
     *
     * @return the HTTP status error code returned from the Terra server
     */
    public int getStatus() {
        return status;
    }

    /**
     * The message generated by the Terra server is usually reasonable enough
     * to show to end users of the software.
     *
     * @return the error message generated by the Terra server
     */
    @Override
    @JsonProperty("error")
    public String getMessage() {
        return super.getMessage();
    }

    /**
     * When trying to create a meme like a category or option, and a duplicate
     * slug is found already existing in Terra, a Conflict is raised and
     * returned.  When this happens, the conflicting meme is returned with
     * the error message, and will be included in the Duplicate property of
     * the exception.
     *
     * @return the meme that generated the duplicate exception
     */
    public Meme getDuplicate() {
        return duplicate;
    }
}

