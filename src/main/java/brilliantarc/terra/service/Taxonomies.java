package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.*;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

/**
 * Service requests related to taxonomies.  Don't instantiate this directly; use
 * Client.Service.taxonomies() instead.
 */
public class Taxonomies extends Base {

    public Taxonomies(Client.Services services) {
        super(services);
    }

    /**
     * Look up the taxonomies currently available for the given operating
     * company.
     *
     * @param opco The three or four letter code for the operating company, e.g. "PKT"
     *
     * @return A list of Taxonomy objects
     *
     * @throws SingularityException The operating company suggested does not exist
     */
    public List<Taxonomy> all(String opco) {
        return services.request().to("taxonomies").send("opco", opco).as(new TypeReference<List<Taxonomy>>() {
        });
    }

    /**
     * Look for details about a specific taxonomy.  Note that this call
     * simply returns the basic information, such as the name and language
     * of the taxonomy.  It does not return child categories or mappings.
     *
     * It is useful for confirming the existence of a taxonomy.
     *
     * @param opco The three or four letter code for the operating company, e.g. "PKT"
     * @param slug The slug identifier for the taxonomy, unique to the operating company
     *
     * @return A Taxonomy
     *
     * @throws SingularityException Either the operating company or taxonomy does not exist
     */
    public Taxonomy taxonomy(String opco, String slug) {
        return services.request().to("taxonomy").send("opco", opco, "slug", slug).as(Taxonomy.class);
    }

    /**
     * Create a new taxonomy for the given operating company.  The taxonomy
     * name need not be unique, but its (optional) slug must be.
     *
     * If the slug is not provided (either null or an empty string), it will
     * be created by transforming the name of the taxonomy into an SEO-ready
     * value.  For example, the name "Travel, Accommodations, and Food
     * Services" would be transformed into "travel-accommodation-and-food-services".
     *
     * If language is not supplied it will default to the language of the
     * operating company.
     *
     * May throw an exception in any of the following situations:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     * * If the operating company does not exist, returns a status of Not Found.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the taxonomy
     * @param slug     A unique identifier for the taxonomy, or null to have one generated
     * @param language The language of the taxonomy, or null to use the opco's language
     *
     * @return The newly created Taxonomy object
     *
     * @throws SingularityException see above
     */
    public Taxonomy create(String opco, String name, String slug, String language) {
        return services.request().to("taxonomy").post().send("opco", opco, "name", name, "slug", slug,
                "lang", language).as(Taxonomy.class);
    }

    /**
     * Create the taxonomy using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The name of the taxonomy
     * @param slug A unique identifier for the taxonomy, or null to have one generated
     *
     * @return The newly created Taxonomy object
     *
     * @throws SingularityException see the create(opco, name, slug, language) method
     */
    public Taxonomy create(String opco, String name, String slug) {
        return create(opco, name, slug, null);
    }

    /**
     * Create the taxonomy using the default language of the operating company.
     * Will also generate a slug based on the name of the taxonomy.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The name of the taxonomy
     *
     * @return The newly created Taxonomy object
     *
     * @throws SingularityException see the create(opco, name, slug, language) method
     */
    public Taxonomy create(String opco, String name) {
        return create(opco, name, null, null);
    }

    /**
     * Update's the name and language of the given Taxonomy with the server.
     * Will not change the slug or operating company however.  If you modify
     * either of those, mostly likely you will receive a Not Found status in
     * the ServerException, indicating the Taxonomy object could not be found
     * on the Terra server.
     *
     * Note that this method will return a new Taxonomy object.  The original
     * object will not be modified.
     *
     * If there is a problem updating the taxonomy, an error will be returned
     * from the server which will raise a SingularityException.  The status
     * code (and the message) will indicate the problem.
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable
     * * If the existing taxonomy could not be found on the server, returns a status of Not Found
     * * If the local taxonomy is older than the version on the server, a Precondition Failed status is returned
     *
     * @param taxonomy An existing taxonomy with its name or language modified
     *
     * @return A new Taxonomy object with the updated information, as confirmation
     *
     * @throws SingularityException see above
     */
    public Taxonomy update(Taxonomy taxonomy) {
        return services.request().to("taxonomy").put().send("opco", taxonomy.getOpco(),
                "name", taxonomy.getName(), "slug", taxonomy.getSlug(), "lang", taxonomy.getLanguage(),
                "external", taxonomy.getExternal(), "v", taxonomy.getVersion()).as(Taxonomy.class);
    }

    /**
     * Completely deletes the given taxonomy from the Terra server.  If
     * there are any categories associated with the taxonomy, they are
     * orphaned, as are any properties and options associated with the
     * taxonomy.
     *
     * This method returns nothing.  If the delete is unsuccessful, an
     * exception will be raised.  Otherwise it completed successfully.
     *
     * @param taxonomy The taxonomy to delete; only opco and slug are used
     *
     * @throws SingularityException throws a Not Found exception if the
     *                              taxonomy does not exist, or a Precondition Failed if the
     *                              taxonomy on the server is newer than the one submitted
     */
    public void delete(Taxonomy taxonomy) {
        services.request().to("taxonomy").delete().send("opco", taxonomy.getOpco(), "slug", taxonomy.getSlug(),
                "v", taxonomy.getVersion());
    }

    /**
     * Get the list of synonyms associated with this taxonomy.
     *
     * @param taxonomy The taxonomy
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The taxonomy does not exist
     */
    public List<Synonym> synonyms(Taxonomy taxonomy) {
        return services.request().to("taxonomy/synonyms").send("opco", taxonomy.getOpco(),
                "slug", taxonomy.getSlug()).as(new TypeReference<List<Synonym>>() {
        });
    }

    /**
     * Create a new synonym and associate it with this taxonomy.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the taxonomy.
     *
     * @param taxonomy The taxonomy to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     * @param language The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The taxonomy does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Taxonomy taxonomy, String name, String slug, String language) {
        return services.request().to("taxonomy/synonym").post().send("opco", taxonomy.getOpco(),
                "name", name, "slug", slug, "lang", language, "taxonomy", taxonomy.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this taxonomy.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the taxonomy.
     *
     * Defaults to the language of the operating company.
     *
     * @param taxonomy The taxonomy to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return the newly created Synonym
     *
     * @throws SingularityException The taxonomy does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Taxonomy taxonomy, String name, String slug) {
        return createSynonym(taxonomy, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this taxonomy.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the taxonomy.
     *
     * Defaults to the language of the operating company, and the slug will be
     * generated from the name.
     *
     * @param taxonomy The taxonomy to associate with the synonym
     * @param name     The human-readable name of the synonym
     *
     * @return the newly created Synonym
     *
     * @throws SingularityException The taxonomy does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Taxonomy taxonomy, String name) {
        return createSynonym(taxonomy, name, null, null);
    }

    /**
     * Associate an existing synonym with a taxonomy.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and taxonomy must belong to the same operating
     * company.
     *
     * @param taxonomy The taxonomy with which to associate the synonym
     * @param synonym  The synonym for the taxonomy
     *
     * @throws SingularityException Either the taxonomy or the synonym doesn't exist
     */
    public void addSynonym(Taxonomy taxonomy, Synonym synonym) {
        services.request().to("taxonomy/synonym").put().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a taxonomy.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the taxonomy.
     *
     * @param taxonomy The taxonomy to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     * @param language The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The taxonomy does not exist
     */
    public Synonym addSynonym(Taxonomy taxonomy, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the taxonomy?
            Synonym existing = services.synonyms().synonym(taxonomy.getOpco(), slug);
            addSynonym(taxonomy, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(taxonomy, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a taxonomy.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the taxonomy.
     *
     * Defaults to the language of the operating company.
     *
     * @param taxonomy The taxonomy to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The taxonomy does not exist
     */
    public Synonym addSynonym(Taxonomy taxonomy, String synonym) {
        return addSynonym(taxonomy, synonym, null);
    }

    /**
     * Remove the synonym with from the taxonomy.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * taxonomy.
     *
     * @param taxonomy The taxonomy with which to disassociate the synonym
     * @param synonym  The synonym for the taxonomy
     *
     * @throws SingularityException Either the taxonomy or the synonym doesn't exist
     */
    public void removeSynonym(Taxonomy taxonomy, Synonym synonym) {
        services.request().to("taxonomy/synonym").delete().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "slug", synonym.getSlug());
    }

    /**
     * Get the top-level categories for the given taxonomy.  Note that this
     * does not return all the categories for a taxonomy.
     *
     * This is the same call present in the Terra.Service.Categories class.
     * It is here as a convenience.
     * </summary>
     * <seealso cref="Terra.Service.Categories.Children(Terra.Category)"/>
     *
     * @param taxonomy The taxonomy of categories
     *
     * @return A list of Category objects
     *
     * @throws SingularityException The given taxonomy does not exist
     */
    public List<Category> children(Taxonomy taxonomy) {
        return services.categories().children(taxonomy);
    }

    /**
     * Look up the properties associated with the given taxonomy.  The list
     * returned does not include options, and there may be properties in
     * the list that have no options associated with them anyway.
     *
     * @param taxonomy The taxonomy with which the properties are associated
     *
     * @return A list of Property objects
     *
     * @throws SingularityException The taxonomy does not exist
     */
    public List<Property> properties(Taxonomy taxonomy) {
        return services.request().to("taxonomy/properties").send("opco", taxonomy.getOpco(),
                "slug", taxonomy.getSlug()).as(new TypeReference<List<Property>>() {
        });
    }

    /**
     * Associate a property with a taxonomy.  This let's both users and
     * the software know what properties should be included in queries
     * such as inheritance.  It also serves as a visual tool for users,
     * so they know the list of possible properties from which to select
     * when adding options.
     *
     * @param taxonomy The taxonomy to which to add the property
     * @param property The property to associate
     *
     * @throws SingularityException Either the property or the taxonomy doesn't exist
     */
    public void addProperty(Taxonomy taxonomy, Property property) {
        services.request().to("taxonomy/property").put().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "property", property.getSlug());
    }

    /**
     * Remove a property from a taxonomy.  Note that this does not remove
     * any of the relations between options and this node.  It does,
     * however, filter the options from any requests that use the properties
     * associated with the node as a filter, such as inheritance.
     *
     * @param taxonomy The taxonomy from which to remove the property
     * @param property The property to remove
     *
     * @throws SingularityException Either the property or the taxonomy doesn't exist
     */
    public void removeProperty(Taxonomy taxonomy, Property property) {
        services.request().to("taxonomy/property").delete().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "property", property.getSlug());
    }

    /**
     * Find all the options associated with this taxonomy.
     *
     * This operation can be a bit slow, as the system has to filter and
     * collate options and properties.  It is typically faster to request
     * a list of properties, then request the options for a selected
     * property (this is how the Terra UI does things).  Of course by
     * "slow", we mean takes around 300ms.
     *
     * @param taxonomy The taxonomy of options to retrieve
     *
     * @return A list of Property objects with their Options list filled
     *
     * @throws SingularityException The taxonomy doesn't exist
     */
    public List<Property> options(Taxonomy taxonomy) {
        return services.request().to("taxonomy/options").send("opco", taxonomy.getOpco(),
                "slug", taxonomy.getSlug()).as(new TypeReference<List<Property>>() {
        });
    }

    /**
     * Add an option to a taxonomy.  The taxonomy, option, and property
     * must all exist in the same operating company.
     *
     * @param taxonomy The taxonomy to associate the option
     * @param property The property or "verb" used in the relation
     * @param option   The option being related
     *
     * @throws SingularityException Either the option, property or the taxonomy doesn't exist
     */
    public void addOption(Taxonomy taxonomy, Property property, Option option) {
        services.request().to("taxonomy/option").put().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "property", property.getSlug(), "option", option.getSlug());
    }

    /**
     * Remove an option from a taxonomy.  The taxonomy, option, and property
     * must all exist in the same operating company.
     *
     * @param taxonomy The taxonomy from which to remove the option
     * @param property The property or "verb" used in the relation
     * @param option   The option being removed
     *
     * @throws SingularityException Either the option, property or the taxonomy doesn't exist
     */
    public void removeOption(Taxonomy taxonomy, Property property, Option option) {
        services.request().to("taxonomy/option").delete().send("opco", taxonomy.getOpco(),
                "taxonomy", taxonomy.getSlug(), "property", property.getSlug(), "option", option.getSlug());
    }

}
