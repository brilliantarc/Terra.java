package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.User;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

/**
 * Manage user accounts.  You may also use this service to interact with
 * users.
 *
 * Service requests related to users.  Don't instantiate this directly; use
 * Client.Service.users() instead.
 */
public class Users extends Base {

    public Users(Client.Services services) {
        super(services);
    }

    /**
     * Return the list of all the user accounts in the Terra server.
     *
     * @return A list of users
     */
    public List<User> all() {
        return get("users").as(new TypeReference<List<User>>() {});
    }

    /**
     * Look up a user's account information, including email address.  Details
     * on a user may be limited based on your own account level.
     * Administrators receive more information about a user than general
     * users may request.
     *
     * @param login The user's account login
     *
     * @return Details about a user
     *
     * @throws SingularityException the user does not exist
     */
    public User user(String login) {
        return get("user").send("login", login).as(User.class);
    }

    /**
     * Create a new user account.  All fields except the opco are required.
     * The user's email must be unique in the system.
     *
     * If the opco is indicated, the user will be associated with the
     * operating company.  This will limit the user's write-access to that
     * portfolio.
     *
     * If a user does not belong to a portfolio, and is not a "Super" user,
     * he or she will not have access to write any information.
     *
     * @param login                The user's login; may not contain spaces or non-ASCII characters
     * @param email                The user's email address; forgotten passwords and messages will be forwarded to this email
     * @param password             A password for the user
     * @param passwordConfirmation Confirm the password
     * @param opco                 The three or four letter code of the portfolio to which to associate this user (optional)
     *
     * @return The newly created user account
     *
     * @throws SingularityException If any of the required information is missing or incorrect
     */
    public User create(String login, String email, String password, String passwordConfirmation, String opco) {
        return post("user").send("login", login, "password", password, "confirmation", passwordConfirmation,
                "email", email, "opco", opco).as(User.class);
    }

    /**
     * Create a general user, unassociated with a specific operating company.
     * Unless this user is made a "Super" user, he or she will not be able to
     * perform any work in the system (since the account is not associated
     * with an operating company)."
     *
     * @param login                The user's login; may not contain spaces or non-ASCII characters
     * @param email                The user's email address; forgotten passwords and messages will be forwarded to this email
     * @param password             A password for the user
     * @param passwordConfirmation Confirm the password
     *
     * @return The newly created user account
     *
     * @throws SingularityException If any of the required information is missing or incorrect
     */
    public User create(String login, String email, String password, String passwordConfirmation) {
        return create(login, email, password, passwordConfirmation, null);
    }

    /**
     * Update a user's email address.  A quick way to update a user's email.
     * Must be an administrator.
     *
     * @param login The existing user login
     * @param email The new email address
     *
     * @return The updated user information
     *
     * @throws SingularityException The user's login does not exist, or the email is already in use
     */
    public User updateEmail(String login, String email) {
        return put("user/email").send("login", login, "email", email).as(User.class);
    }

    /**
     * Update a user's password.  A quick way to update a user's password.
     *
     * @param login                   The existing user login
     * @param original                The original password
     * @param newPassword             The new password
     * @param newPasswordConfirmation The new password confirmation
     *
     * @return The updated user information
     *
     * @throws SingularityException The user does not exist, or the password is incorrect
     */
    public User updatePassword(String login, String original, String newPassword, String newPasswordConfirmation) {
        return put("user/password").send("login", login, "original", original, "password", newPassword,
                "confirmation", newPasswordConfirmation).as(User.class);
    }

    /**
     * Update a user's account, email and password simultaneously,
     * typically by an admin.
     *
     * A user may not change his or her login.
     *
     * @param user The updated user information
     *
     * @return The updated user information
     *
     * @throws SingularityException The user does not exist, or some of the updated information is incorrect
     */
    public User update(User user) {
        return put("user").send("login", user.getLogin(), "email", user.getEmail(), "password", user.getPassword(),
                "confirmation", user.getPasswordConfirmation()).as(User.class);
    }

    /**
     * Add the user to the given role.  A user may only promote a user
     * to the same level has they are.  For example, an "Administrator"
     * may promote a user to "Administrator", but not "Super".
     *
     * @param user The user account
     * @param role The role, either "Administrator" or "Super"
     *
     * @throws SingularityException The user does not exist, or the role is incorrect
     */
    public void addRole(User user, String role) {
        put("role/user").send("role", role, "login", user.getLogin());
    }

    /**
     * Remove the user from the given role.  A user may only remove other
     * users from roles they themselves belong to.  For example, an
     * "Administrator" may remove a user from the "Administrator" role,
     * but not from the "Super" role.
     *
     * @param user The user to modify
     * @param role The role to add the user to, either "Administrator" or "Super"
     *
     * @throws SingularityException The user does not exist, or the role is incorrect
     */
    public void removeRole(User user, String role) {
        delete("role/user").send("role", role, "login", user.getLogin());
    }

    /**
     * Enable the user's account, i.e. allow him or her to login to Terra.
     *
     * @param user The user to enable
     *
     * @return The updated user account information
     *
     * @throws SingularityException The user does not exist
     */
    public User enable(User user) {
        return put("user/enable").send("login", user.getLogin()).as(User.class);
    }

    /**
     * Disable the user's account, i.e. preventing him or her from logging
     * into Terra.
     *
     * @param user The user to disable
     *
     * @return The updated user account information
     *
     * @throws SingularityException The user does not exist
     */
    public User disable(User user) {
        return put("user/disable").send("login", user.getLogin()).as(User.class);
    }

    /**
     * Send an email message to the given user.
     *
     * @param to      The user to send the email to
     * @param message A brief message to the user
     * @param subject The subject of the message; optional
     *
     * @throws SingularityException The user does not exist
     */
    public void sendMessage(User to, String message, String subject) {
        post("user/message").send("to", to.getLogin(), "subject", subject, "message", message);
    }

    /**
     * Send an email message to the given user.  With a standard/default subject.
     *
     * @param to      The user to send the email to
     * @param message A brief message to the user
     *
     * @throws SingularityException The user does not exist
     */
    public void sendMessage(User to, String message) {
        sendMessage(to, message, null);
    }

    /**
     * Looks for an email address in Terra and send the user information
     * on updating his or her password.  The user will be emailed a link
     * to the Terra client application's "Update Password" screen, just
     * as if they had clicked on "Forgot Password" in Terra.
     *
     * @param email The user's email address
     */
    public void forgotPassword(String email) {
        put("user/forgot").send("email", email);
    }

    /**
     * Return the history of changes made to the ontology by the given user.
     *
     * @param user The user account
     * @param from Starting point of results to return; defaults to 0
     * @param max  The maximum number of results to return; defaults to 100
     *
     * @return A list of changes made to the ontology
     *
     * @throws SingularityException The user does not exist
     */
    public List<String> history(User user, int from, int max) {
        return get("user/history").send("login", user.getLogin(), "from", Integer.toString(from),
                "max", Integer.toString(max)).as(new TypeReference<List<String>>() {});
    }

    /**
     * Return the history of changes made to the ontology by the given user.
     * Returns 100 results max.
     *
     * @param user The user account
     * @param from Starting point of results to return; defaults to 0
     *
     * @return A list of changes made to the ontology
     *
     * @throws SingularityException The user does not exist
     */
    public List<String> history(User user, int from) {
        return history(user, from, 100);
    }

    /**
     * Return the history of changes made to the ontology by the given user.
     * Returns the first 100 results by defalt.
     *
     * @param user The user account
     *
     * @return A list of changes made to the ontology
     *
     * @throws SingularityException The user does not exist
     */
    public List<String> history(User user) {
        return history(user, 0, 100);
    }
}
