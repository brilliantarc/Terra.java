package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.node.*;
import org.codehaus.jackson.type.TypeReference;
import brilliantarc.terra.SingularityException;

import java.util.List;

/**
 * Service requests related to operating companies.  Don't instantiate this
 * directly; use Client.Service.operatingCompanies() instead.
 */
public class OperatingCompanies {

    private Client.Services services;

    public OperatingCompanies(Client.Services services) {
        this.services = services;
    }

    /**
     * Retrieve the available operating companies from Terra.
     *
     * @return A list of operating companies
     */
    public List<OperatingCompany> all() {
        return services.request().to("opcos").send().as(new TypeReference<List<OperatingCompany>>() {
        });
    }

    /**
     * Retrieve the details about an operating company, such as its name and
     * default language.
     *
     * @param opco The three or four letter identifier for the operating
     *             company
     *
     * @return The operating company
     *
     * @throws SingularityException An operating company does not exist for the
     *                              given slug
     */
    public OperatingCompany operatingCompany(String opco) {
        return services.request().to("opco").send("opco", opco).as(OperatingCompany.class);
    }

//    /**
//     * Convenience method to return all the taxonomies for an operating
//     * company.
//     *
//     * @param opco The three or four letter operating company code
//     * @throws SingularityException The operating company does not exist
//     * @return A list of taxonomies
//     */
//    public List<Taxonomy> Taxonomies(String opco) {
//        return _client.Taxonomies.All(opco);
//    }
//
//    /**
//     * Convenience method to return all the properties for an operating
//     * company.
//     * </summary>
//     * <seealso cref="Terra.Service.Properties.All"/>
//     *
//     * @param opco The three or four letter operating company code
//     * @throws SingularityException The operating company does not exist
//     * @return A list of properties
//     */
//    public List<Property> Properties(string opco) {
//        return _client.Properties.All(opco);
//    }
//
//    /**
//     * Convenience method to return all the superheadings for an operating
//     * company.
//     * </summary>
//     * <seealso cref="Terra.Service.Superheadings.All"/>
//     *
//     * @param opco The three or four letter operating company code
//     * @throws SingularityException The operating company does not exist
//     * @return A list of superheadings
//     */
//    public List<Superheading> Superheadings(string opco) {
//        return _client.Superheadings.All(opco);
//    }
//
//    /**
//     * Convenience method to return all the headings for an operating
//     * company.
//     * </summary>
//     * <seealso cref="Terra.Service.Headings.All"/>
//     *
//     * @param opco The three or four letter operating company code
//     * @throws SingularityException The operating company does not exist
//     * @return A list of headings
//     */
//    public List<Heading> Headings(string opco) {
//        return _client.Headings.All(opco);
//    }
//
//    /**
//     * Add a user to the operating company.  This will grant the user
//     * write-access to information associated with the opco.
//     * </summary>
//     *
//     * @param opco The three or four letter operating company code
//     * @param user The user account to associate with the opco
//     */
//    public void AddUser(string opco, User user) {
//        _client.Request("opco/user", Method.PUT).
//                AddParameter("opco", opco).
//                AddParameter("login", user.Login).
//                MakeRequest();
//    }
//
//    /**
//     * Remove a user from the operating company.  This will deny the user
//     * write-access to the content in that opco's portfolio.
//     * </summary>
//     *
//     * @param opco The three or four letter operating company code
//     * @param user The user account to disassociate with the opco
//     */
//    public void RemoveUser(string opco, User user) {
//        _client.Request("opco/user", Method.DELETE).
//                AddParameter("opco", opco).
//                AddParameter("login", user.Login).
//                MakeRequest();
//    }
//

    /**
     * Get the history of changes made across the entire operating company, to
     * every category, property, option, taxonomy, heading and even
     * superheading.
     *
     * @param opco The three or four letter code for the operating company
     * @param from The starting result to return
     * @param max  The maximum number of results to return
     *
     * @return The list of changes made to each piece of information in Terra,
     *         for this opco
     */
    public List<String> history(String opco, int from, int max) {
        return services.request().to("opco/history").send("opco", opco, "from", from,
                "max", max).as(new TypeReference<List<String>>() {
        });
    }

    /**
     * Get the history of changes made across the entire operating company, to
     * every category, property, option, taxonomy, heading and even
     * superheading.
     *
     * Defaults to retrieving the most recent 100 (max) items.
     *
     * @param opco The three or four letter code for the operating company
     *
     * @return The list of changes made to each piece of information in Terra,
     *         for this opco
     */
    public List<String> history(String opco) {
        return history(opco, 0, 100);
    }
}
