package brilliantarc.terra.service;

import brilliantarc.terra.Client;
import brilliantarc.terra.SingularityException;
import brilliantarc.terra.node.Heading;
import brilliantarc.terra.node.Superheading;
import brilliantarc.terra.node.Synonym;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

/**
 * Superheadings provide for a very limited grouping of headings within
 * Terra, to reduce the amount of information that has to come back when
 * working with headings.
 *
 * Service requests related to superheadings.  Don't instantiate this directly; use
 * Client.Service.superheadings() instead.
 */
public class Superheadings extends Base {

    public Superheadings(Client.Services services) {
        super(services);
    }

    /**
     * Returns all the superheadings associated with the given opco.
     *
     * @param opco The three or four letter code for the operating company
     *
     * @return A list of Superheadings
     *
     * @throws SingularityException The operating company does not exist
     */
    public List<Superheading> all(String opco) {
        return get("superheadings").send("opco", opco).as(new TypeReference<List<Superheading>>() {});
    }

    /**
     * Look up a superheading by its slug.  Useful for checking to see if an
     * superheading exists.
     *
     * @param opco The three or four letter code for the opco
     * @param slug The superheadings's slug
     *
     * @return An Superheading object
     *
     * @throws SingularityException The operating company or superheading does not exist
     */
    public Superheading superheading(String opco, String slug) {
        return get("superheading").send("opco", opco, "slug", slug).as(Superheading.class);
    }

    /**
     * Create a new superheading.
     *
     * If there is a problem creating a superheading, a SingularityException
     * is thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the slug already exists, returns a status of Conflict.
     * * If the operating company, relatedTo, or relatedBy does not exist, returns a status of Not Found.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this superheading
     * @param slug     A unique slug for this superheading; superheadingal, will be generated from the name if not provided
     * @param external An third-party external identifier for this superheading (superheadingal)
     * @param language The two-letter ISO language for the superheading's name; defaults to the opco's language
     *
     * @return The newly created Superheading
     *
     * @throws SingularityException
     */
    public Superheading create(String opco, String name, String slug, String external, String language) {
        return post("superheading").send("opco", opco, "name", name, "slug", slug, "external", external, "lang", language).as(Superheading.class);
    }

    /**
     * Create a new superheading without a third-party external identifier and without
     * relating it to anything.
     *
     * @param opco     The three or four letter code for the operating company
     * @param name     The human-readable name for this superheading
     * @param slug     A unique slug for this superheading; superheadingal, will be generated from the name if not provided
     * @param language The two-letter ISO language for the superheading's name; defaults to the opco's language
     *
     * @return The newly created Superheading
     *
     * @throws SingularityException see above
     */
    public Superheading create(String opco, String name, String slug, String language) {
        return create(opco, name, slug, null, language);
    }

    /**
     * Create an superheading using the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this superheading
     * @param slug A unique slug for this superheading; superheadingal, will be generated from the name if not provided
     *
     * @return The newly created Superheading
     *
     * @throws SingularityException see above
     */
    public Superheading create(String opco, String name, String slug) {
        return create(opco, name, slug, null, null);
    }

    /**
     * Create an superheading with a generated SEO-compliant slug, based on the name,
     * in the default language of the operating company.
     *
     * @param opco The three or four letter code for the operating company
     * @param name The human-readable name for this superheading
     *
     * @return The newly created Superheading
     *
     * @throws SingularityException see above
     */
    public Superheading create(String opco, String name) {
        return create(opco, name, null, null, null);
    }


    /**
     * Update the name, external identifier, or language for this superheading.
     * Neither the operating company nor the slug may be modified.
     *
     * If there is a problem updating the property on the server, a
     * SingularityException is thrown:
     *
     * * If any of the parameters submitted are invalid, returns a status of Not Acceptable.
     * * If the existing superheading could not be found on the server, returns a status of Not Found
     * * If the local superheading is older than the version on the server, a Precondition Failed status is returned
     *
     * @param superheading An superheading with the updated information
     *
     * @return A new Superheading object, with the updates in place
     *
     * @throws SingularityException see above
     */
    public Superheading update(Superheading superheading) {
        return put("superheading").send("opco", superheading.getOpco(), "name", superheading.getName(),
                "slug", superheading.getSlug(), "external", superheading.getExternal(),
                "lang", superheading.getLanguage(), "v", superheading.getVersion()).as(Superheading.class);
    }

    /**
     * Completely delete a superheading from Terra.  If you delete a
     * superheading, the headings will still be available from the
     * operating company itself.
     *
     * @param superheading The superheading to delete
     *
     * @throws SingularityException The superheading does not exist
     */
    public void delete(Superheading superheading) {
        delete("superheading").send("opco", superheading.getOpco(), "slug", superheading.getSlug());
    }

    /**
     * Get the list of synonyms associated with this superheading.
     *
     * @param superheading The superheading
     *
     * @return A list of synonyms
     *
     * @throws SingularityException The superheading does not exist
     */
    public List<Synonym> synonyms(Superheading superheading) {
        return get("superheading/synonyms").send("opco", superheading.getOpco(),
                "slug", superheading.getSlug()).as(new TypeReference<List<Synonym>>() {});
    }

    /**
     * Create a new synonym and associate it with this superheading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the superheading.
     *
     * @param superheading The superheading to associate with the synonym
     * @param name         The human-readable name of the synonym
     * @param slug         An SEO-compliant slug for the synonym; generated if not provided
     * @param language     The language of the name; defaults to the opco's language
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The superheading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Superheading superheading, String name, String slug, String language) {
        return post("superheading/synonym").send("opco", superheading.getOpco(), "name", name, "slug", slug,
                "language", language, "superheading", superheading.getSlug()).as(Synonym.class);
    }

    /**
     * Create a new synonym and associate it with this superheading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the superheading.
     *
     * Defaults to the language of the operating company.
     *
     * @param superheading The superheading to associate with the synonym
     * @param name         The human-readable name of the synonym
     * @param slug         An SEO-compliant slug for the synonym; generated if not provided
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The superheading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Superheading superheading, String name, String slug) {
        return createSynonym(superheading, name, slug, null);
    }

    /**
     * Create a new synonym and associate it with this superheading.
     *
     * Synonyms may also be used as a simple translation tool.  When you
     * create a synonym, by default it is assigned to the same langauge as
     * the operating company.  However, by assigning a different language
     * to the synonym, you now have a translation for the superheading.
     *
     * Defaults to the language of the operating company, and an SEO-compliant
     * slug based on the synonym's name.
     *
     * @param superheading The superheading to associate with the synonym
     * @param name         The human-readable name of the synonym
     *
     * @return The newly created Synonym
     *
     * @throws SingularityException The superheading does not exist, or the synonym already exists
     */
    public Synonym createSynonym(Superheading superheading, String name) {
        return createSynonym(superheading, name, null, null);
    }

    /**
     * Associate an existing synonym with a superheading.  This is a rare call
     * to make.  Typically you want createSynonym or the addSynonym method
     * that takes a synonym name (String) instead of a Synonym object.
     *
     * Both the synonym and superheading must belong to the same operating
     * company.
     *
     * @param superheading The superheading with which to associate the synonym
     * @param synonym      The synonym for the superheading
     *
     * @throws SingularityException Either the superheading or the synonym doesn't exist
     */
    public void addSynonym(Superheading superheading, Synonym synonym) {
        put("superheading/synonym").send("opco", superheading.getOpco(), "superheading", superheading.getSlug(),
                "synonym", synonym.getSlug());
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a superheading.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the superheading.
     *
     * @param superheading The superheading to associate with this synonym
     * @param synonym      The new or existing name of a synonym
     * @param language     The language of the synonym; defaults to the opco's language
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The superheading does not exist
     */
    public Synonym addSynonym(Superheading superheading, String synonym, String language) {
        String slug = services.slugify(synonym);
        try {
            // Relate an existing synonym to the superheading?
            Synonym existing = services.synonyms().synonym(superheading.getOpco(), slug);
            addSynonym(superheading, existing);
            return existing;
        } catch (SingularityException e) {
            // Create a new synonym with the given name
            if (e.getStatus() == 404) {
                return createSynonym(superheading, synonym, slug, language);
            } else {
                throw e;
            }
        }
    }

    /**
     * This is a convenience method to create or add an existing synonym
     * (or translation) to a superheading.  If the synonym does not already
     * exist, it is created with a default slug (and default language, if
     * not otherwise indicated).  The synonym, existing or new, is
     * associated with the superheading.
     *
     * Defaults to the language of the operating company.
     *
     * @param superheading The superheading to associate with this synonym
     * @param synonym      The new or existing name of a synonym
     *
     * @return The new or existing synonym
     *
     * @throws SingularityException The superheading does not exist
     */
    public Synonym addSynonym(Superheading superheading, String synonym) {
        return addSynonym(superheading, synonym, null);
    }

    /**
     * Remove the synonym with from the superheading.  Note that this doesn't
     * delete the synonym, simply removes its association from this
     * superheading.
     *
     * @param superheading The superheading with which to disassociate the synonym
     * @param synonym      The synonym for the superheading
     *
     * @throws SingularityException Either the superheading or the synonym doesn't exist
     */
    public void removeSynonym(Superheading superheading, Synonym synonym) {
        delete("superheading/synonym").send("opco", superheading.getOpco(), "superheading", superheading.getSlug(), "slug", synonym.getSlug());
    }

    /**
     * Retrieve the headings associated with this superheading.
     *
     * @param superheading The superheading parent
     *
     * @return A list of headings
     *
     * @throws SingularityException the superheading doesn't exist
     */
    public List<Heading> headings(Superheading superheading) {
        return get("superheading/headings").send("opco", superheading.getOpco(), "slug", superheading.getSlug()).as(new TypeReference<List<Heading>>() {});
    }

    /**
     * Add a heading to the given superheading.  You may repeatedly add
     * a heading to a superheading with no ill or adverse effects, and
     * no exceptions will be thrown.
     *
     * @param superheading The parent superheading
     * @param heading      The child heading
     *
     * @throws SingularityException Either the superheading or the heading doesn't exist
     */
    public void addHeading(Superheading superheading, Heading heading) {
        put("superheading/heading").send("opco", superheading.getOpco(), "superheading", superheading.getSlug(),
                "heading", heading.getSlug());
    }

    /**
     * Remove the heading from the superheading.  Removing a heading from
     * a superheading that doesn't have that heading has no effect and
     * generates no exceptions.
     *
     * @param superheading The superheading parent
     * @param heading      The child heading
     *
     * @throws SingularityException Either the superheading or the heading doesn't exist
     */
    public void removeHeading(Superheading superheading, Heading heading) {
        delete("superheading/heading").send("opco", superheading.getOpco(), "superheading", superheading.getSlug(),
                "heading", heading.getSlug());
    }
}
