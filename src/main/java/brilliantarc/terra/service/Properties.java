package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.*;
import brilliantarc.terra.server.Request;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * Properties represent the relations between an option and a category or
 * taxonomy.
 *
 * In fact, properties do not truly relate options to categories or
 * taxonomies.  Rather, the relation between an option and a category
 * is identified by a type of relation--what we refer to as a "verb".
 * Associating a property with this relation means creating a property
 * that has the same slug as this verb.  The property then represents
 * the relation between the category and option.
 *
 * Associating the property with a category or taxonomy also allows
 * for a visual indicator to users as to the possible properties used
 * in that category or taxonomy.  And properties are used as a filter,
 * to isolate these random verbs from the specific relations used to
 * model behaviors in Terra, such as "mapped-to" or "child-of".  It
 * should also be noted these reserved relations are not permitted
 * to be created as properties.
 *
 * Due to the complex relationship between a Property, a "verb" and
 * categories, taxonomies, and options, <b>properties may not be deleted</b>.
 * They may be created and renamed, but not removed.  A future release
 * may support the removal of properties, but for now the safest course
 * of action is to simply limit the destruction of properties.
 *
 * Service requests related to properties.  Don't instantiate this directly; use
 * Client.Service.properties() instead.
 */
public class Properties extends Base {

    public Properties(Client.Services services) {
        super(services);
    }

    /**
     * Retrieve all the currently defined properties for the entire
     * operating company.
     *
     * @param opco The three or four letter code for the opco
     *
     * @return A list of Property objects
     *
     * @throws SingularityException The operating company does not exist
     */
    public List<Property> all(String opco) {
        return get("properties").send("opco", opco).as(new TypeReference<List<Property>>() {});
    }

    /**
     * Look up a property by its slug.  Useful for checking to see if a
     * property exists.
     *
     * @param opco The three or four letter code for the opco
     * @param slug The property's slug
     *
     * @return A Property object
     *
     * @throws SingularityException The operating company or property does not exist
     */
    public Property property(String opco, String slug) {
        return get("property").send("opco", opco, "slug", slug).as(Property.class);
    }

    /**
     * Create a new property.  Typically a property is created as it is
     * needed, and associated with a category or taxonomy so it can be
     * used to map options to either of those memes.  If you include the
     * relatedTo, it will automatically map the property to that meme.
     *
     * The taxonomy or category to which you are relating this property
     * must exist in the same operating company as the property.
     *
     * The property's slug will be generated from the name if not otherwise
     * indicated.
     *
     * If there is a problem creating the property, a SingularityException
     * is thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     * * If the operating company or relatedTo does not exist, returns a status of Not Found.
     *
     * @param opco      The three or four letter code for the operating company
     * @param name      The human-readable name for this property
     * @param slug      The relation type, or "verb" for this property
     * @param external  An third-party external identifier for this property (optional)
     * @param language  The two-letter ISO language for the property; defaults to the opco's language
     * @param relatedTo The category or taxonomy using this property (optional)
     *
     * @return The newly created Property
     *
     * @throws SingularityException see above
     */
    public Property create(String opco, String name, String slug, String external, String language, Meme relatedTo) {
        Map<String, Object> params = Request.createMap("opco", opco, "name", name, "slug", slug,
                "external", external, "lang", language);

        if (relatedTo instanceof Taxonomy) {
            params.put("taxonomy", relatedTo.getSlug());
        } else if (relatedTo instanceof Category) {
            params.put("category", relatedTo.getSlug());
        }

        return post("property").send(params).as(Property.class);
    }

    /**
     * Create a new property without relating it to anything.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this property
     * @param slug     A unique slug for this property; propertyal, will be generated from the name if not provided
     * @param external An third-party external identifier for this property (propertyal)
     * @param language The two-letter ISO language for the property's name; defaults to the opco's language
     *
     * @return The newly created Property
     *
     * @throws SingularityException see above
     */
    public Property create(String opco, String name, String slug, String external, String language) {
        return create(opco, name, slug, external, language, null);
    }

    /**
     * Create a new property without a third-party external identifier and without
     * relating it to anything.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this property
     * @param slug     A unique slug for this property; propertyal, will be generated from the name if not provided
     * @param language The two-letter ISO language for the property's name; defaults to the opco's language
     *
     * @return The newly created Property
     *
     * @throws SingularityException see above
     */
    public Property create(String opco, String name, String slug, String language) {
        return create(opco, name, slug, null, language, null);
    }

    /**
     * Create an property using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this property
     * @param slug A unique slug for this property; propertyal, will be generated from the name if not provided
     *
     * @return The newly created Property
     *
     * @throws SingularityException see above
     */
    public Property create(String opco, String name, String slug) {
        return create(opco, name, slug, null, null, null);
    }

    /**
     * Create an property with a generated SEO-compliant slug, based on the name,
     * in the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this property
     *
     * @return The newly created Property
     *
     * @throws SingularityException see above
     */
    public Property create(String opco, String name) {
        return create(opco, name, null, null, null, null);
    }

    /**
     * Update the name, external identifier, or language for this property.
     * Neither the operating company nor the slug may be modified.
     *
     * If the property cannot be updated for some reason, a SingularityException
     * is thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing property could not be found on the server, returns a status of Not Found
     * * If the local property is older than the version on the server, a Precondition Failed status is returned
     *
     * @param property A property with the updated information
     *
     * @return A new Property object, with the updates in place
     *
     * @throws SingularityException see above
     */
    public Property update(Property property) {
        return put("property").send("opco", property.getOpco(), "name", property.getName(),
                "slug", property.getSlug(), "external", property.getExternal(),
                "lang", property.getLanguage(), "v", property.getVersion()).as(Property.class);
    }

    /**
     * Get the list of synonyms associated with this property.
     *
     * @param property The property
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The property does not exist
     */
    public List<Synonym> synonyms(Property property) {
        return get("property/synonyms").send("opco", property.getOpco(),
                "slug", property.getSlug()).as(new TypeReference<List<Synonym>>() {});
    }

    /**
     * Create a new synonym and associate it with this property.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the property.
     *
     * @param property The property to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     * @param language The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The property does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Property property, String name, String slug, String language) {
        return post("property/synonym").send("opco", property.getOpco(), "name", name, "slug", slug,
                "language", language, "property", property.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this property.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the property.
     *
     * Defaults to the language of the operating company.
     *
     * @param property The property to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The property does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Property property, String name, String slug) {
        return createSynonym(property, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this property.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the property.
     *
     * Defaults to the language of the operating company, and an SEO-compliant
     * slug based on the synonym's name.
     *
     * @param property The property to associate with the synonym
     * @param name     The human-readable name of the synonym
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The property does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Property property, String name) {
        return createSynonym(property, name, null, null);
    }

    /**
     * Associate an existing synonym with a property.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and property must belong to the same operating
     * company.
     *
     * @param property The property with which to associate the synonym
     * @param synonym  The synonym for the property
     *
     * @throws SingularityException Either the property or the synonym doesn't exist
     */
    public void addSynonym(Property property, Synonym synonym) {
        put("property/synonym").send("opco", property.getOpco(), "property", property.getSlug(),
                "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a property.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the property.
     *
     * @param property The property to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     * @param language The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The property does not exist
     */
    public Synonym addSynonym(Property property, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the property?
            Synonym existing = services.synonyms().synonym(property.getOpco(), slug);
            addSynonym(property, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(property, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a property.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the property.
     *
     * Defaults to the language of the operating company.
     *
     * @param property The property to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The property does not exist
     */
    public Synonym addSynonym(Property property, String synonym) {
        return addSynonym(property, synonym, null);
    }

    /**
     * Remove the synonym with from the property.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * property.
     *
     * @param property The property with which to disassociate the synonym
     * @param synonym  The synonym for the property
     *
     * @throws SingularityException Either the property or the synonym doesn't exist
     */
    public void removeSynonym(Property property, Synonym synonym) {
        delete("property/synonym").send("opco", property.getOpco(), "property", property.getSlug(), "slug", synonym.getSlug());
    }
}
