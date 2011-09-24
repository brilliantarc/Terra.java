package brilliantarc.terra.server;

/**
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
 */
public enum Status {

    NOT_FOUND (404),
    DUPLICATE(409),
    MISSING_REQUIRED(424),
    INVALID_PARAMETER(406),
    INVALID_RELATION(406),
    IN_USE(423),
    VERSION_MISMATCH(412),
    AUTHENTICATION_FAILED(401),
    USER_NOT_FOUND(404),
    AUTHORIZATION_FAILED(403),
    NOT_IMPLEMENTED(501);

    public final int code;

    Status(int code) {
        this.code = code;
    }
}
