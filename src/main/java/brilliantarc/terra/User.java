package brilliantarc.terra;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * A user account on the Terra server.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {

    private String login;
    private String email;
    private List<String> roles;
    private String password;
    private String passwordConfirmation;
    private boolean disabled;
    private String credentials;

    /**
     * @return a user's account login
     */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return a user's email address; used to send messages and forgotten
     *         password notifications
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * We currently only support two in Terra:  "Super" and "Administrator".
     * An "Administrator" may create and update user information in an operating
     * company, while a "Super" may do this system-wide.
     *
     * @return the role names for the user
     */
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * This is never returned by a call to Terra, but it may be used to update
     * a user's password.
     *
     * @return a user's password, if set locally on the client
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This is never returned by a call to Terra, but it may be used to update
     * a user's password.
     *
     * @return a user's password confirmation, if set locally on the client
     */
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    /**
     * Accounts in Terra may not be deleted, but they can be disabled.  A
     * disabled user will remain in the system and be available for historic
     * purposes, but that user will not be allowed to login to the system
     * under any circumstances.
     *
     * @return  true, if the user's account has been disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * In order to limit the traffic of passwords over the network, upon
     * successful authentication a user is issued a temporary credential.  This
     * credential is valid for 24 hours, and subsequent requests to authenticate
     * will return the same credentials.  After authentication, send these
     * credentials with every call to Terra.
     *
     * @return  the user's API credentials
     */
    public String getCredentials() {
        return credentials;
    }

    @JsonProperty(value="user_credentials")
    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
