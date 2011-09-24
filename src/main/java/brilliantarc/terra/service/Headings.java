package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.*;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

/**
 * Service requests related to headings.  Don't instantiate this directly; use
 * Client.Service.headings() instead.
 */
public class Headings extends Base {

    public Headings(Client.Services services) {
        super(services);
    }

    /**
     * Get all the headings associated with the given operating company.
     * This request can take a while, given the large number of headings
     * associated with each operating company.
     *
     * @param opco The operating company to look towards for headings
     *
     * @return A list of Heading objects
     *
     * @throws SingularityException The given operating company does not exist
     */
    public List<Heading> all(String opco) {
        return get("headings").as(new TypeReference<List<Heading>>() {});
    }

    /**
     * Look for details about a specific heading.  Note that this call
     * simply returns the basic information, such as the name and language
     * of the heading.  It does not return mappings.
     *
     * It is useful for confirming the existence of a heading.
     *
     * @param opco The three or four letter code for the operating company, e.g. "PKT"
     * @param pid  The PID identifier for the heading, unique to the operating company
     *
     * @return A heading
     *
     * @throws SingularityException Either the operating company or heading does not exist
     */
    public Heading heading(String opco, String pid) {
        return get("heading").send("opco", opco, "pid", pid).as(Heading.class);
    }

    /**
     * Create a new heading for the given operating company.  The heading
     * name need not be unique, but its PID must be.
     *
     * If the PID is not provided (either null or an empty string), it will
     * be created by transforming the name of the heading into an SEO-ready
     * value.  For example, the name "Mexican Restaurants" would be transformed
     * into "mexican-restaurants".
     *
     * If language is not supplied it will default to the language of the
     * operating company.
     *
     * If there is a problem creating the heading on the server, a
     * SingularityException is thrown, with one of the following possible status
     * values:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the PID already exists, returns a status of Conflict.
     * * If the operating company or superheading does not exist, returns a status of Not Found.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the heading
     * @param pid      A unique identifier for the heading, or null to have one generated
     * @param language The language of the heading, or null to use the opco's language
     * @param parent   A superheading, to add this new heading directly to a superheading
     *
     * @return The newly created Heading object
     *
     * @throws SingularityException see above
     */
    public Heading create(String opco, String name, String pid, String language, Superheading parent) {
        return post("heading").send("opco", opco, "name", name, "pid", pid, "lang", language,
                "superheading", parent != null ? parent.getSlug() : null).as(Heading.class);
    }

    /**
     * Create a new heading without placing the heading under a superheading.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The name of the heading
     * @param pid      A unique identifier for the heading, or null to have one generated
     * @param language The language of the heading, or null to use the opco's language
     *
     * @return The newly created Heading object
     *
     * @throws SingularityException see above
     */
    public Heading create(String opco, String name, String pid, String language) {
        return create(opco, name, pid, language, null);
    }

    /**
     * Create a new heading using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The name of the heading
     * @param pid  A unique identifier for the heading, or null to have one generated
     *
     * @return The newly created Heading object
     *
     * @throws SingularityException see above
     */
    public Heading create(String opco, String name, String pid) {
        return create(opco, name, pid, null, null);
    }

    /**
     * Create a new heading using the default language of the operating company.
     * Generates a PID as an SEO-compliant string based on the name of the
     * heading.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The name of the heading
     *
     * @return The newly created Heading object
     *
     * @throws SingularityException see above
     */
    public Heading create(String opco, String name) {
        return create(opco, name, null, null, null);
    }

    /**
     * Update's the name and language of the given heading with the server.
     * Will not change the PID or operating company however.  If you modify
     * either of those, mostly likely you will receive a Not Found status in
     * the ServerException, indicating the heading object could not be found
     * on the Terra server.
     *
     * Note that this method will return a new Heading object.  The original
     * object will not be modified.
     *
     * If the server has a problem updating the heading, such as an invalid
     * value or a duplicate, one of the following errors may be returned:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing heading could not be found on the server, returns a status of Not Found
     * * If the local heading is older than the version on the server, a Precondition Failed status is returned
     *
     * @param heading An existing heading with its name or language modified
     *
     * @return A new Heading object with the updated information, as confirmation
     *
     * @throws SingularityException if the heading cannot be updated; see
     *                              above
     */
    public Heading update(Heading heading) {
        return put("heading").send("opco", heading.getOpco(), "name", heading.getName(), "pid", heading.getPid(),
                "lang", heading.getLanguage(), "v", heading.getVersion()).as(Heading.class);
    }

    /**
     * Completely deletes the given heading from the Terra server.
     *
     * This method returns nothing.  If the delete is unsuccessful, an
     * exception will be raised.  Otherwise it completed successfully.
     *
     * @param heading The heading to delete; only Opco and Pid are used
     *
     * @throws SingularityException raises a Not Found exception if the heading does not exist, or
     *                              a Precondition Failed if the heading on the server is newer than
     *                              the one submitted.
     */
    public void delete(Heading heading) {
        delete("heading").send("opco", heading.getOpco(), "pid", heading.getPid(), "v", heading.getVersion());
    }

    /**
     * Get the list of synonyms associated with this heading.
     *
     * @param heading The heading
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The heading does not exist
     */
    public List<Synonym> synonyms(Heading heading) {
        return get("heading/synonyms").send("opco", heading.getOpco(), "pid", heading.getPid()).as(new TypeReference<List<Synonym>>() {});
    }

    /**
     * Create a new synonym and associate it with this heading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the heading.
     *
     * @param heading  The heading to associate with the synonym
     * @param name     The human-readable name of the synonym
     * @param slug     An SEO-compliant slug for the synonym; generated if not provided
     * @param language The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The heading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Heading heading, String name, String slug, String language) {
        return post("heading/synonym").send("opco", heading.getOpco(), "name", name, "slug", slug,
                "language", language, "heading", heading.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this heading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the heading.
     *
     * Defaults to the language of the operating company.
     *
     * @param heading The heading to associate with the synonym
     * @param name    The human-readable name of the synonym
     * @param slug    An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The heading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Heading heading, String name, String slug) {
        return createSynonym(heading, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this heading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the heading.
     *
     * Defaults to the language of the operating company, and an SEO-compliant
     * slug based on the synonym's name.
     *
     * @param heading The heading to associate with the synonym
     * @param name    The human-readable name of the synonym
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The heading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Heading heading, String name) {
        return createSynonym(heading, name, null, null);
    }

    /**
     * Associate an existing synonym with a heading.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and heading must belong to the same operating
     * company.
     *
     * @param heading The heading with which to associate the synonym
     * @param synonym The synonym for the heading
     *
     * @throws SingularityException Either the heading or the synonym doesn't exist
     */
    public void addSynonym(Heading heading, Synonym synonym) {
        put("heading/synonym").send("opco", heading.getOpco(), "heading", heading.getSlug(),
                "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a heading.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the heading.
     *
     * @param heading  The heading to associate with this synonym
     * @param synonym  The new or existing name of a synonym
     * @param language The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The heading does not exist
     */
    public Synonym addSynonym(Heading heading, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the heading?
            Synonym existing = services.synonyms().synonym(heading.getOpco(), slug);
            addSynonym(heading, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(heading, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a heading.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the heading.
     *
     * Defaults to the language of the operating company.
     *
     * @param heading The heading to associate with this synonym
     * @param synonym The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The heading does not exist
     */
    public Synonym addSynonym(Heading heading, String synonym) {
        return addSynonym(heading, synonym, null);
    }

    /**
     * Remove the synonym with from the heading.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * heading.
     *
     * @param heading The heading with which to disassociate the synonym
     * @param synonym The synonym for the heading
     *
     * @throws SingularityException Either the heading or the synonym doesn't exist
     */
    public void removeSynonym(Heading heading, Synonym synonym) {
        delete("heading/synonym").send("opco", heading.getOpco(),
                "heading", heading.getSlug(), "slug", synonym.getSlug());
    }

    /**
     * Get the superheadings to which this heading belongs.
     *
     * @param heading The child heading
     *
     * @return A list of Superheadings
     *
     * @throws SingularityException The heading doesn't exist
     */
    public List<Superheading> parents(Heading heading) {
        return get("heading/parents").send("opco", heading.getOpco(), "pid", heading.getPid()).as(new TypeReference<List<Superheading>>() {});
    }

    /**
     * Look for the categories to which the given heading has been mapped.
     * In otherwords, what heading-to-category mappings exists for which
     * this heading is the subject of the relation?
     *
     * @param heading The subject heading of the relations
     *
     * @return A list of object categories
     *
     * @throws SingularityException The subject heading does not exist
     */
    public List<Category> mappedTo(Heading heading) {
        return get("heading/mappings").send("opco", heading.getOpco(), "pid", heading.getPid()).as(new TypeReference<List<Category>>() {});
    }

    /**
     * Map a heading to a category.  The heading will inherit the properties
     * and options from the category.  Note that you may map a heading to
     * many different categories, and inherit the sum total of properties
     * and options from those categories.
     *
     * For reference, the relation between a heading and a category is
     * defined as "heading-for".
     *
     * This is just a convenience method that does the exact same thing as
     * Categories.mapHeading.
     *
     * @param from The heading (subject)
     * @param to   The category (object)
     *
     * @throws SingularityException Either the heading or the category does not exist or could not be mapped
     */
    public void mapHeading(Heading from, Category to) {
        services.categories().mapHeading(from, to);
    }

    /**
     * Remove the relation between a heading and a category.
     *
     * This is just a convenience method that does the exact same thing as
     * Categories.unapHeading.
     *
     * @param from The heading (subject)
     * @param to   The category (object)
     *
     * @throws SingularityException Either the category or heading does not exist or could not be unmapped
     */
    public void UnmapHeading(Heading from, Category to) {
        services.categories().unmapHeading(from, to);
    }

    /**
     * Retrieve the full inheritance of properties and options for this
     * heading, based on the mappings to other categories and those
     * categories' own inheritance.
     *
     * @param heading The heading for which to retrieve inheritance
     *
     * @return A list of Property objects, with inherited Options included
     *
     * @throws SingularityException The heading does not exist
     */
    public List<Property> inheritance(Heading heading) {
        return Property.fromJsonArray(get("heading/inheritance").send("opco", heading.getOpco(), "pid", heading.getPid()).root());
    }
}
